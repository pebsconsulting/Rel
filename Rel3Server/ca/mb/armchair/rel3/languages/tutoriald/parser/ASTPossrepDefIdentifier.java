/* Generated By:JJTree: Do not edit this line. ASTPossrepDefIdentifier.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=ca.mb.armchair.rel3.languages.tutoriald.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package ca.mb.armchair.rel3.languages.tutoriald.parser;

public
class ASTPossrepDefIdentifier extends SimpleNode {
  public ASTPossrepDefIdentifier(int id) {
    super(id);
  }

  public ASTPossrepDefIdentifier(TutorialD p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TutorialDVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=69c3155ce3d78f1be8f2d143b0a8b04b (do not edit this line) */