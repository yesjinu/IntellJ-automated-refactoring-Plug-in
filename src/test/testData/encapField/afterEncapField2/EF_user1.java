public class EF_user1 {
    private EF_owner o = new EF_owner();

    public int memberUse()
    {
        int i = 0;  // Irrelevant

        int x = o.getValue(); // DeclarationStatement

        i = o.getValue();  // ExpressionStatement - AssignmentExpression

        System.out.println(o.getValue()); // ExpressionStatement - MethodCallExpression

        if(o.getValue() ==0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = o.getValue() -1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(o.getValue()) // SwitchStatement - keyword
        {
            case 0: i = 2; i = o.getValue();
        }

        for(int j = 0; j < o.getValue(); j++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return o.getValue() -1; // ReturnStatement - BinaryExpression
    }

    public void memberDef(int x)
    {
        o.setValue(x); // ExpressionStatement - AssignmentExpression

        if(x==0)
        {
            o.setValue(2); // IfStatement - BlockStatement - ExpressionStatement - AssignmentExpression
        }

        switch(x)
        {
            case 0: o.setValue(3); // SwitchStatement - codeblock - .. - assignment expression
        }

        o.setValue((2 > x) ? 2 : x); // expressions statement - assignment expression - reference expression
    }
}
