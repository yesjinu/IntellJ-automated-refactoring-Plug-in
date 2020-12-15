package wanted.refactoring;

/**
 * Singleton Class with managing features of Multiple BaseRefactorActions.
 *
 * @author Mintae Kim
 */
public class BaseRefactorManager {
    private static BaseRefactorManager manager = null;

    /* Design Pattern: Singleton */
    protected BaseRefactorManager () { }

    public static BaseRefactorManager getInstance() {
        if (manager == null)
            manager = new BaseRefactorManager();
        return manager;
    }

    /**
     * Method that fetches Refactoring Method name by ID.
     *
     * @param id Refactoring Techinque ID
     * @return Corresponding Refactoring name (story name)
     */
    public BaseRefactorAction getRefactorActionByID (String id) {
        switch (id) {
            // Scope: Class
            case "IFM":
                return new IntroduceForeignMethodAction();
            case "ILE":
                return new IntroduceLocalExtensionAction();
            case "HD":
                return new HideDelegateAction();


            // Scope: Field
            case "SEF":
                return new SelfEncapField();
            case "EF":
                return new EncapField();

            // Scope: Method
            case "IM":
                return new InlineMethod();
            case "IMS":
                return new InlineMethodStrengthen();
            case "RPA":
                return new RemoveUnusedParameterAction();

            // Scope: Statement
            case "EV":
                return new ExtractVariable();
            case "CCE":
                return new ConsolidateCondExpr();
            case "CDCF":
                return new ConsolidateDupCondFrag();
            case "INA":
                return new IntroduceAssertion();

            // Scope: expression
            case "RMN":
                return new ReplaceMagicNumber();
            case "PWO":
                return new ParameterizeWholeObjectAction();

            default:
                return null;
        }
    }
}
