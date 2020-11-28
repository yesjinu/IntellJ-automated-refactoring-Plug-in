public class SEF_advance {
    private int me<caret>mber;

    public SEF_advance()
    {
        member = 0;
    }

    public int memberUse()
    {
        int i = 0;  // Irrelevant

        int x = member; // DeclarationStatement

        i = member;  // ExpressionStatement - AssignmentExpression

        System.out.println(member); // ExpressionStatement - MethodCallExpression

        if(member==0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = member-1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(member) // SwitchStatement - keyword
        {
            case 0: i = 2; i = member;
        }

        for(int j = 0; j < member; j++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return member-1; // ReturnStatement - BinaryExpression
    }

    public void memberDef(int x)
    {
        member = x; // ExpressionStatement - AssignmentExpression

        if(x==0)
        {
            member = 2; // IfStatement - BlockStatement - ExpressionStatement - AssignmentExpression
        }

        switch(x)
        {
            case 0: member = 3; // SwitchStatement - codeblock - .. - assignment expression
        }

        member = (2>x)?2:x; // expressions statement - assignment expression - reference expression
    }

}
