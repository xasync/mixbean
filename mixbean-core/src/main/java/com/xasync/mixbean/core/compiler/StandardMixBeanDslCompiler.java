/*
Copyright 2022~Forever xasync.com under one or more contributor authorized.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.xasync.mixbean.core.compiler;

import com.xasync.mixbean.core.BizFuncBeanDslSpec;
import com.xasync.mixbean.core.MixBeanDslCompiler;
import com.xasync.mixbean.core.MixBeanTrack;
import com.xasync.mixbean.core.exception.MixBeanSyntaxException;
import com.xasync.mixbean.core.util.LogUtils;
import com.xasync.mixbean.core.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static com.xasync.mixbean.core.MixBeanDslSymbols.*;
import static com.xasync.mixbean.core.exception.MixBeanSyntaxErrorEnum.*;

/**
 * StandardMixBeanDslCompiler
 *
 * @author xasync.com
 */
public class StandardMixBeanDslCompiler implements MixBeanDslCompiler {

    @Override
    public String name() {
        return "std";
    }

    @Override
    public List<Object> compile(String dsl) {
        String newDsl = StringUtils.trimToEmpty(dsl);
        if (newDsl.isEmpty()) {
            LogUtils.debug("{} compile a blank dsl '{}'", MixBeanTrack.current(), dsl);
            return Collections.emptyList();
        }
        /* attempt to determine whether the dsl describes a serial Service-Orchestration.If so,
        use a low-cost way to compile. the effective rule is whether there is a parallel delimiter in the dsl*/
        boolean isPureSerialDsl = !newDsl.contains(PARALLEL_BEAN_DELIMITER);
        if (isPureSerialDsl) {
            return parsePureSerialDsl(newDsl);
        }
        /* dealing with complex nesting of serial and parallel */
        int len = newDsl.length();
        // a cursor about compiling
        int index = 0;
        // end cursor of the previous parallel block
        int preIndex = 0;
        List<Object> blocks = new ArrayList<>();
        for (; index < len; ) {
            String c = String.valueOf(newDsl.charAt(index));
            // judge whether the current character is the start mark of parallel block
            if (Objects.equals(PARALLEL_BLOCK_START, c)) {
                Triple<String, Integer, Integer> triple = findParallelBlock(newDsl, index);
                String parallelBlock = triple.getLeft();
                int pBlockEndPos = triple.getRight();
                // can't a parallel block from the offset in dsl, then move the cursor to the next position
                if (Objects.isNull(parallelBlock)) {
                    index += 1;
                    continue;
                }
                //if the current index is after the preIndex when the finding is success, then there is a serial blocks
                if (index > preIndex) {
                    String pureSerialDsl = newDsl.substring(preIndex, index);
                    pureSerialDsl = StringUtils.strip(pureSerialDsl, SERIAL_BEAN_DELIMITER);
                    if (StringUtils.isNotBlank(pureSerialDsl)) {
                        blocks.addAll(parsePureSerialDsl(pureSerialDsl));
                    }
                }
                //compile the parallel block found and add it into the result set.
                blocks.add(parseParallelDsl(parallelBlock));
                //move cursor to the end position of the parallel block found
                index = pBlockEndPos;
                preIndex = pBlockEndPos;
            }
            //move the cursor to the next position
            index += 1;
            //needs to the rest sub-string when the last cursor and must be a serial block.
            if (index >= len) {
                String pureSerialDsl = newDsl.substring(preIndex, index);
                pureSerialDsl = StringUtils.strip(pureSerialDsl, SERIAL_BEAN_DELIMITER);
                if (StringUtils.isNotBlank(pureSerialDsl)) {
                    blocks.addAll(parsePureSerialDsl(pureSerialDsl));
                }
            }
        }
        //
        return blocks;
    }


    /**
     * Parsing the parallel block
     *
     * @param dsl the parallel block in MixBean's DSL
     * @return Map
     */
    private Object parseParallelDsl(String dsl) {
        String parallelDslLine = stripParallelBlockMarks(dsl);
        if (StringUtils.isBlank(parallelDslLine)) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), UNEXPECTED_BLANK_PARALLEL_DSL,
                    String.format("the parallel syntax is error '%s'", dsl));
        }
        //check if the parallel block nest the parallel block
        Triple<String, Integer, Integer> triple = findParallelBlock(dsl, 0);
        boolean isLineNestParallelBlock = Objects.nonNull(triple.getLeft());
        if (isLineNestParallelBlock) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), PARALLEL_NESTING_NOT_SUPPORT,
                    String.format("the parallel block nesting the parallel block isn't support at now '%s'", dsl));
        }
        //split the dsl by PARALLEL_BEAN_DELIMITER
        String[] blocks = StringUtils.split(parallelDslLine, PARALLEL_BEAN_DELIMITER);
        Map<String, Object> parallelMap = new HashMap<>(blocks.length);
        for (int index = 0; index < blocks.length; index++) {
            String block = blocks[index];
            List<Object> list = parsePureSerialDsl(block);
            parallelMap.put("block" + index, list);
        }
        return parallelMap;
    }


    /**
     * parsing DSL with serial combination only
     *
     * @param dsl MixBean's DSL
     * @return the blocks which will be invoked in serial
     */
    private List<Object> parsePureSerialDsl(String dsl) {
        //just one block then return it directly
        boolean onlyOneBizFunc = !dsl.contains(SERIAL_BEAN_DELIMITER);
        if (onlyOneBizFunc) {
            return Collections.singletonList(parseBizFuncDslSpec(dsl, null, null));

        }
        //multiple blocks then split it
        List<Object> dataList = new ArrayList<>();
        String[] blocks = dsl.split(SERIAL_BEAN_DELIMITER);
        for (int index = 0; index < blocks.length; index++) {
            String preBlock = index > 0 ? blocks[index - 1] : null;
            String block = blocks[index];
            String nextBlock = index < blocks.length - 1 ? blocks[index + 1] : null;
            if (StringUtils.isBlank(block)) {
                LogUtils.debug("{} the dsl about BizFuncBean is blank causing a duplicate delimiter.'{}^^^{}^^^{}'",
                        MixBeanTrack.current(), preBlock, block, nextBlock);
                continue;
            }
            //parse the dsl block about BizFuncBean and add it into the result set
            BizFuncBeanDslSpec bizFuncDslSpec = parseBizFuncDslSpec(block, preBlock, nextBlock);
            dataList.add(bizFuncDslSpec);
        }
        return dataList;
    }

    /**
     * Parsing the dsl block about BizFuncBean
     *
     * @param block     the dsl block about BizFuncBean
     * @param preBlock  the previous block
     * @param nextBlock the next block
     * @return {@code BizFuncBeanDslSpec}
     */
    private BizFuncBeanDslSpec parseBizFuncDslSpec(String block, String preBlock, String nextBlock) {
        String newBlock = StringUtils.trimToEmpty(block);
        if (newBlock.isEmpty()) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), BIZ_FUNC_BEAN_UNEXPECTED_BLANK,
                    String.format("the dsl block about BizFuncBean is blank '%s^^^%s^^^%s'", preBlock, block, nextBlock));
        }
        int paramStartPos = newBlock.indexOf(BEAN_PARAM_LIST_START);
        int paramEndPos = newBlock.lastIndexOf(BEAN_PARAM_LIST_END);
        boolean invalidParamSyntax = paramStartPos < 0 || paramEndPos < 0 || paramEndPos <= paramStartPos;
        if (invalidParamSyntax) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), PARAM_LIST_SYNTAX_ERROR,
                    String.format("the parameter syntax is error '%s'", block));
        }
        int paramEndSplitPos = Math.min(paramEndPos + BEAN_PARAM_LIST_END.length(), newBlock.length());
        String nameAndCtlLine = StringUtils.trimToEmpty(newBlock.substring(0, paramStartPos));
        String paramsLine = StringUtils.trimToEmpty(newBlock.substring(paramStartPos, paramEndSplitPos));
        String aliasLine = StringUtils.trimToEmpty(newBlock.substring(paramEndSplitPos));
        // extract alias if exists
        String alias = null;
        if (StringUtils.isNotBlank(aliasLine)) {
            if (aliasLine.startsWith(BEAN_ALIAS_MARK)) {
                alias = StringUtils.stripStart(aliasLine, BEAN_ALIAS_MARK);
                alias = StringUtils.trimToNull(alias);
                if (Objects.isNull(alias)) {
                    LogUtils.warn("the alias syntax is error because the alias is blank '{}'", block);
                }
            } else {
                throw new MixBeanSyntaxException(MixBeanTrack.current(), BIZ_FUNC_BEAN_ALIAS_MISS_SYMBOL,
                        String.format("the alias syntax is error because of missing '%s' in '%s'", BEAN_ALIAS_MARK, block));
            }
        }
        // extract params
        String params = StringUtils.stripStart(paramsLine, BEAN_PARAM_LIST_START);
        params = StringUtils.stripEnd(params, BEAN_PARAM_LIST_END);
        // extract soft-depend mark
        boolean softDepend = nameAndCtlLine.endsWith(BEAN_SOFT_DEPEND_MARK);
        if (softDepend) {
            //eat dsl line
            nameAndCtlLine = StringUtils.stripEnd(nameAndCtlLine, BEAN_SOFT_DEPEND_MARK);
        }
        //extract timeout if exist
        Long timeout = null;
        int tPos = nameAndCtlLine.lastIndexOf(BEAN_TIMEOUT_MARK);
        if (tPos > 0) {
            try {
                int subPos = Math.min(tPos + BEAN_TIMEOUT_MARK.length(), newBlock.length());
                timeout = Long.parseLong(nameAndCtlLine.substring(subPos));
            } catch (Throwable ex) {
                throw new MixBeanSyntaxException(MixBeanTrack.current(), BIZ_FUNC_BEAN_TIMEOUT_PARSE_FAIL,
                        String.format("the timeout must be a long type '%s'", block));
            }
            //eat dsl line
            nameAndCtlLine = nameAndCtlLine.substring(0, tPos);
        }
        //extract ability
        String ability;
        String provider = null;
        int delimiterPos = nameAndCtlLine.indexOf(ABILITY_PROVIDER_DELIMITER);
        if (delimiterPos < 0) {
            ability = nameAndCtlLine.trim();
        } else {
            ability = nameAndCtlLine.substring(0, delimiterPos).trim();
            provider = nameAndCtlLine.substring(delimiterPos + ABILITY_PROVIDER_DELIMITER.length()).trim();
        }
        if (ability.isEmpty()) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), BIZ_FUNC_BEAN_MISS_ABILITY,
                    String.format("miss ability's name '%s'", block));
        }
        if (Objects.nonNull(provider) && provider.isEmpty()) {
            throw new MixBeanSyntaxException(MixBeanTrack.current(), BIZ_FUNC_BEAN_MISS_PROVIDER,
                    String.format("miss provider's name '%s'", block));
        }
        return new BizFuncBeanDslSpec(newBlock, ability, alias, provider, timeout, softDepend, params);
    }


    /**
     * Extracts the first complete parallel block backward from the specified offset of the dsl.
     *
     * @param dsl    MixBean's DSL
     * @param offset start position of backward lookup
     * @return a complete parallel block, Triple:(parallel-block,start-index,end-index)
     */
    private static Triple<String, Integer, Integer> findParallelBlock(String dsl, int offset) {
        String newDsl = StringUtils.trimToEmpty(dsl);
        if (newDsl.isEmpty()) {
            return Triple.of(null, -1, -1);
        }
        int len = newDsl.length();
        int index = offset;
        for (; index < len; ) {
            String preStr = newDsl.substring(Math.max(0, index - 5), index);
            String c = String.valueOf(newDsl.charAt(index));
            String nextStr = newDsl.substring(index, Math.min(index + 5, len));
            //success to find the start of parallel block
            if (Objects.equals(PARALLEL_BLOCK_START, c)) {
                int pBlockEndPos = findParallelBlockEndIndex(newDsl, index);
                //miss the end mark in the parallel block
                if (pBlockEndPos < 0) {
                    throw new MixBeanSyntaxException(MixBeanTrack.current(), PARALLEL_MISS_END_SYMBOL,
                            String.format("miss %s for the parallel block '%s^^^%s^^^%s'",
                                    PARALLEL_BLOCK_END, preStr, c, nextStr));
                }
                //check if the front of parallel block is exist SERIAL_BEAN_DELIMITER
                boolean nearbySerialDelimiter = SERIAL_BEAN_DELIMITER.equals(
                        TextUtils.subStrForwardSkipWhitespace(newDsl, index, SERIAL_BEAN_DELIMITER.length()));
                //If there is no SERIAL_BEAN_DELIMITER in front of the block, then check the back of block
                if (!nearbySerialDelimiter) {
                    nearbySerialDelimiter = SERIAL_BEAN_DELIMITER.equals(
                            TextUtils.subStrBackwardSkipWhitespace(newDsl, pBlockEndPos, SERIAL_BEAN_DELIMITER.length()));
                }
                //if the block is not nearby SERIAL_BEAN_DELIMITER, it means that the current block isn't a parallel block
                if (!nearbySerialDelimiter) {
                    // move cursor to next position
                    index += 1;
                    continue;
                }
                // success to find the first parallel block
                return Triple.of(dsl.substring(index, pBlockEndPos), index, pBlockEndPos);
            }
            // move cursor to next position
            index += 1;
        }
        return Triple.of(null, -1, -1);
    }

    /**
     * Find the end position of the parallel block based on the feature of stack
     *
     * @param dsl    MixBean's DSL
     * @param offset the start position for finding
     * @return the end position of the parallel block
     */
    private static int findParallelBlockEndIndex(String dsl, int offset) {
        Stack<String> stack = new Stack<>();
        //push the start mark into stack
        stack.push(PARALLEL_BLOCK_START);
        int len = dsl.length();
        int index = offset + 1;
        while (!stack.isEmpty() && index < len) {
            String c = String.valueOf(dsl.charAt(index));
            if (Objects.equals(PARALLEL_BLOCK_START, c)) {
                //push the char into stack if it's the start mark of parallel block
                stack.push(c);
            } else if (Objects.equals(PARALLEL_BLOCK_END, c)) {
                //pop an element of stack if it's the end mark of parallel block
                stack.pop();
            }
            index += 1;
        }
        //the finding is success only when the stack is empty
        return stack.isEmpty() ? index : -1;
    }

    /**
     * Strip the syntax symbol on both sides of the parallel block
     *
     * @param parallelDsl a parallel block string in MixBean' DSL
     * @return the parallel block which strips the syntax symbol
     */
    private static String stripParallelBlockMarks(String parallelDsl) {
        if (StringUtils.isBlank(parallelDsl)) {
            return null;
        }
        String newParallelDsl = StringUtils.stripStart(parallelDsl, PARALLEL_BLOCK_START);
        newParallelDsl = StringUtils.stripEnd(newParallelDsl, PARALLEL_BLOCK_END);
        return newParallelDsl.trim();
    }

}