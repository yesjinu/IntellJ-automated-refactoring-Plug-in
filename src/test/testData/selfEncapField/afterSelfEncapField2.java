public class SEF_advance {
    private int member;

    public SEF_advance()
    {
        setMember(0);
    }

    public int memberUse()
    {
        int i = 0;  // Irrelevant

        int x = getMember(); // DeclarationStatement

        i = getMember();  // ExpressionStatement - AssignmentExpression

        System.out.println(getMember()); // ExpressionStatement - MethodCallExpression

        if(getMember()==0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = getMember()-1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(getMember()) // SwitchStatement - keyword
        {
            case 0: i = 2; i = getMember();
        }

        for(int j = 0; j < getMember(); j++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return getMember()-1; // ReturnStatement - BinaryExpression
    }

    public void memberDef(int x)
    {
        setMember(x); // ExpressionStatement - AssignmentExpression

        if(x==0)
        {
            setMember(2); // IfStatement - BlockStatement - ExpressionStatement - AssignmentExpression
        }

        switch(x)
        {
            case 0: setMember(3); // SwitchStatement - codeblock - .. - assignment expression
        }

        setMember((2>x)?2:x); // expressions statement - assignment expression - reference expression
    }

    protected int getMember()
    {
        return member;
    }

    protected void setMember(int newValue)
    {
        member = newValue;
    }

}
