/**
 * This file is part of libRibbonIO library (check README).
 * Copyright (C) 2012-2013 Stanislav Nepochatov
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/

package Export;

/**
 * Message formater class for export.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class Formater {
    
    //Reference section
    
    /**
     * Current template string.
     */
    private String currTemplate;
    
    /**
     * Current formatting message.
     */
    private MessageClasses.Message currMessage;
    
    /**
     * Current called directory.
     */
    private String currCalledDir;
    
    /**
     * Current template char array;
     */
    private char[] templateChar;
    
    private java.util.Properties schemaProp;
    
    /**
     * Default constructor.
     * @param givenTemplate template for formating;
     */
    public Formater(java.util.Properties givenProp, String givenTemplate) {
        this.currTemplate = givenTemplate;
        this.schemaProp = givenProp;
    }
    
    /**
     * Build formatted message content according to template.
     * @param givenMessage message to format;
     * @param calledDir message dir;
     * @return formated string.
     */
    public String format(MessageClasses.Message givenMessage, String calledDir) {
        this.currCalledDir = calledDir;
        this.currMessage = givenMessage;
        if (this.templateChar == null) {
            this.templateChar = this.currTemplate.toCharArray();
        }
        StringBuffer formatBuf = new StringBuffer();
        for (Integer charIndex = 0; charIndex < templateChar.length; charIndex++) {
            if (templateChar[charIndex] != '$') {
                formatBuf.append(templateChar[charIndex]);
            } else {
                Integer endIndex = getKeyEnd(charIndex);
                String keyWord = extractKeyword(charIndex + 1, endIndex);
                charIndex = endIndex - 1;
                formatBuf.append(Formater.getByKeyword(keyWord, this));
            }
        }
        this.currCalledDir = null;
        this.currMessage = null;
        return formatBuf.toString();
    }
    
    /**
     * Return index of end of keyword.
     * @param givenIndex index for serch from;
     * @return index of last char of the keyword;
     */
    private Integer getKeyEnd(Integer givenIndex) {
        for (Integer endIndex = givenIndex + 1; endIndex < this.templateChar.length; endIndex++) {
            if (templateChar[endIndex] == ' ' || templateChar[endIndex] == '\r' || templateChar[endIndex] == '\n') {
                return endIndex;
            }
        }
        return this.templateChar.length - 1;
    }
    
    /**
     * Return keyword from char array.
     * @param beginIndex first char of word;
     * @param endIndex last char of word;
     * @return string with keyword;
     */
    private String extractKeyword(Integer beginIndex, Integer endIndex) {
        StringBuffer keyBuf = new StringBuffer();
        while (beginIndex < endIndex) {
            keyBuf.append(this.templateChar[beginIndex]);
            beginIndex++;
        }
        return keyBuf.toString();
    }
    
    //Static section
    
    /**
     * Format keyword class.
     */
    private abstract static class FormatOperation {
        
        /**
         * Keyword.
         */
        public String WORD;
        
        /**
         * Default constructor.
         * @param givenWord given keyword;
         */
        FormatOperation(String givenWord) {
            WORD = givenWord;
        }
        
        /**
         * Return string according to keyword.
         * @param givenFormater formater object;
         * @return formated string;
         */
        public abstract String process(Formater givenFormater);
        
    }
    
    /**
     * Internal static array of FormatOperation instances.
     */
    private final static FormatOperation[] ops = new FormatOperation[] {
        
        /**
         * INDEX keyword: return index of message.
         */
        new FormatOperation("INDEX") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.INDEX;
            }
        },
        
        /**
         * DIR keyword: return export directory name.
         */
        new FormatOperation("DIR") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currCalledDir;
            }
        },
        
        /**
         * TAGS keyword: return tags in form: tag1, tag2, tag3.
         */
        new FormatOperation("TAGS") {
            @Override
            public String process(Formater givenFormater) {
                String compTag = "";
                for (Integer tagIndex=0; tagIndex < givenFormater.currMessage.TAGS.length; tagIndex++) {
                    if (tagIndex == givenFormater.currMessage.TAGS.length - 1) {
                        compTag = compTag + givenFormater.currMessage.TAGS[tagIndex];
                    } else {
                        compTag = compTag + givenFormater.currMessage.TAGS[tagIndex] + ", ";
                    }
                }
                return compTag;
            }
        },
        
        /**
         * DEFIS_TAGS keyword: return tags in form: TAG1-TAG2-TAG3.
         */
        new FormatOperation("DEFIS_TAGS") {
            @Override
            public String process(Formater givenFormater) {
                String compTag = "";
                for (Integer tagIndex=0; tagIndex < givenFormater.currMessage.TAGS.length; tagIndex++) {
                    if (tagIndex == givenFormater.currMessage.TAGS.length - 1) {
                        compTag = compTag + givenFormater.currMessage.TAGS[tagIndex].toUpperCase();
                    } else {
                        compTag = compTag + givenFormater.currMessage.TAGS[tagIndex].toUpperCase() + "-";
                    }
                }
                return compTag;
            }
        },
        
        /**
         * HEADER keyword: return header of message.
         */
        new FormatOperation("HEADER") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.HEADER;
            }
        },
        
        /**
         * UPPER_HEADER keyword: return uppercase header of message.
         */
        new FormatOperation("UPPER_HEADER") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.HEADER.toUpperCase();
            }
        },
        
        /**
         * CONTENT keyword: return content of message.
         */
        new FormatOperation("CONTENT") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.CONTENT;
            }
        },
        
        /**
         * HTML_CONTENT keyword: return content of message with HTML line break.
         */
        new FormatOperation("HTML_CONTENT") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.CONTENT.replaceAll("\n","<br/>");
            }
        },
        
        /**
         * AUTHOR keyword: return author of message (copyright).
         */
        new FormatOperation("AUTHOR") {
            @Override
            public String process(Formater givenFormater) {
                java.util.ListIterator<MessageClasses.MessageProperty> propIter = givenFormater.currMessage.PROPERTIES.listIterator();
                while (propIter.hasNext()) {
                    MessageClasses.MessageProperty currProp = propIter.next();
                    if (currProp.TYPE.equals("COPYRIGHT")) {
                        return currProp.TEXT_MESSAGE;
                    }
                }
                return givenFormater.currMessage.AUTHOR;
            }
        },
        
        /**
         * DATE keyword: return date of message.
         */
        new FormatOperation("DATE") {
            @Override
            public String process(Formater givenFormater) {
                return givenFormater.currMessage.DATE;
            }
        }
    };
    
    /**
     * Get formated string for given keyword.
     * @param givenWord word to format;
     * @param givenFormater formater with data;
     * @return formated string or keyword if word is uknown;
     */
    public static String getByKeyword(String givenWord, Formater givenFormater) {
        for (FormatOperation currOp : ops) {
            if (givenWord.equals(currOp.WORD)) {
                return currOp.process(givenFormater);
            }
        }
        return "$" + givenWord;
    }
}
