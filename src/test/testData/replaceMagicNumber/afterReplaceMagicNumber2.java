public class advance {
    static final double CONSTANT1 = 39876.5432;

    public double method1()
    {
        double i = 0;  // Irrelevant
        int j = 0;

        double x = CONSTANT1; // DeclarationStatement

        i = CONSTANT1;  // ExpressionStatement - AssignmentExpression

        System.out.println(CONSTANT1); // ExpressionStatement - MethodCallExpression

        if(CONSTANT1 -i>0) // IfStatement - BinaryExpression
        { i++; } //  - BlockStatement - CodeBlock - ExpressionStatement - PostfixExpression
        else{ i = CONSTANT1 -1; } //  - BlockStatement - CodeBlock - ExpressionStatement - AssignmentExpression

        switch(j) // SwitchStatement - keyword
        {
            case 0: i = CONSTANT1;
        }

        for(int k = 0; k < CONSTANT1; k++) // ForStatement - BinaryExpression
        {
            i++;
        }

        return CONSTANT1 -1; // ReturnStatement - BinaryExpression
    }
}