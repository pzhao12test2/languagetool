/* JLanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package de.danielnaber.languagetool.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that holds information about where a rule matches text.
 * 
 * @author Daniel Naber
 */
public class RuleMatch implements Comparable {

  private final static Pattern SUGGESTION_PATTERN = Pattern.compile("<em>(.*?)</em>");

  private int fromLine = -1;
  private int column = -1;
  private Rule rule;
  private int fromPos;
  private int toPos;
  private String message;
  private List suggestedReplacements = new ArrayList();
  
  /**
   * Creates a RuleMatch object, taking the rule that triggered
   * this match, position of the match and an explanation message.
   * This message is scanned for &lt;em>...&lt;/em> to get suggested
   * fixes for the problem detected by this rule. 
   */
  public RuleMatch(Rule rule, int fromPos, int toPos, String message) {
    this.rule = rule;
    this.fromPos = fromPos;
    this.toPos = toPos;
    this.message = message;
    // extract suggestion from <em>...</em> in message:
    Matcher matcher = SUGGESTION_PATTERN.matcher(message);
    int pos = 0;
    while (matcher.find(pos)) {
      pos = matcher.end();
      suggestedReplacements.add(matcher.group(1));
    }
  }

  public Rule getRule() {
    return rule;
  }

  /**
   * Set the line number in which the match occurs.
   */
  public void setLine(int fromLine) {
    this.fromLine = fromLine;
  }

  /**
   * Get the line number in which the match occurs.
   */
  public int getLine() {
    return fromLine;
  }

  /**
   * Set the column number in which the match occurs.
   */
  public void setColumn(int column) {
    this.column = column;
  }

  /**
   * Get the line number in which the match occurs.
   */
  public int getColumn() {
    return column;
  }

  /**
   * Position of the start of the error (in characters).
   */
  public int getFromPos() {
    return fromPos;
  }
  
  /**
   * Position of the end of the error (in characters).
   */
  public int getToPos() {
    return toPos;
  }

  /**
   * A short human-readable explanation describing the error.
   */
  public String getMessage() {
    return message;
  }

  /**
   * @see #getSuggestedReplacements()
   */
  public void setSuggestedReplacement(String repl) {
    if (repl == null)
      throw new NullPointerException("replacement might be empty but not null");
    List fixes = new ArrayList();
    fixes.add(repl);
    setSuggestedReplacements(fixes);
  }

  /**
   * @see #getSuggestedReplacements()
   */
  public void setSuggestedReplacements(List repl) {
    if (repl == null)
      throw new NullPointerException("replacement might be empty but not null");
    this.suggestedReplacements = repl;
  }

  /**
   * The text fragments which might be an appropriate fix for the problem. One
   * of these fragments can be used to replace the old text between getFromPos()
   * to getToPos(). Note that by default, text between &lt;em> and &lt;/em> is
   * taken as the suggested replacement. 
   * @return List of String objects or an empty List
   */
  public List getSuggestedReplacements() {
    return suggestedReplacements;
  }

  public String toString() {
    return rule.getId() + ":" + fromPos + "-" + toPos + ":" + message;
  }

  public int compareTo(Object other) {
    if (other == null)
      throw new ClassCastException();
    RuleMatch otherRule = (RuleMatch) other;
    if (getFromPos() < otherRule.getFromPos())
      return -1;
    if (getFromPos() > otherRule.getFromPos())
      return 1;
    return 0;
  }

}
