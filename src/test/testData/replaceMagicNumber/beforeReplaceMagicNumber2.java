public class advance {
    public double method1()
    {
        double i = 0;  // Irrelevant
        int j = 0;

        double x = 39876.543<caret>2; // DeclarationStatement

        i = 39876.5432;  // ExpressionStatement - AssignmentExpression

        System.out.println(39876.5432); // ExpressionStatement - MethodCallExpression

        if(39876.5432-i>0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = 39876.5432-1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(j) // SwitchStatement - keyword
        {
            case 0: i = 39876.5432;
        }

        for(int k = 0; k < 39876.5432; k++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return 39876.5432-1; // ReturnStatement - BinaryExpression
    }
}