public class EF_user1 {
    private EF_owner o = new EF_owner();

    public int memberUse()
    {
        int i = 0;  // Irrelevant

        int x = o.value; // DeclarationStatement

        i = o.value;  // ExpressionStatement - AssignmentExpression

        System.out.println(o.value); // ExpressionStatement - MethodCallExpression

        if(o.value==0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = o.value-1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(o.value) // SwitchStatement - keyword
        {
            case 0: i = 2; i = o.value;
        }

        for(int j = 0; j < o.value; j++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return o.value-1; // ReturnStatement - BinaryExpression
    }

    public void memberDef(int x)
    {
        o.value = x; // ExpressionStatement - AssignmentExpression

        if(x==0)
        {
            o.value = 2; // IfStatement - BlockStatement - ExpressionStatement - AssignmentExpression
        }

        switch(x)
        {
            case 0: o.value = 3; // SwitchStatement - codeblock - .. - assignment expression
        }

        o.value = (2>x)?2:x; // expressions statement - assignment expression - reference expression
    }
}
