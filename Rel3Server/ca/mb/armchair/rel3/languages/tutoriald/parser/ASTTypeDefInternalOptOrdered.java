/* Generated By:JJTree: Do not edit this line. ASTTypeDefInternalOptOrdered.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=ca.mb.armchair.rel3.languages.tutoriald.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package ca.mb.armchair.rel3.languages.tutoriald.parser;

public
class ASTTypeDefInternalOptOrdered extends SimpleNode {
  public ASTTypeDefInternalOptOrdered(int id) {
    super(id);
  }

  public ASTTypeDefInternalOptOrdered(TutorialD p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TutorialDVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0ad28227dff976d1de7a40532b07d4f0 (do not edit this line) */