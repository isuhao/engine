package org.ovirt.engine.core.searchbackend;


public class NetworkClusterConditionFieldAutoCompleter extends BaseConditionFieldAutoCompleter {
    private static final String NETWORK_NAME = "NETWORK_NAME";
    private static final String CLUSTER_NAME = "CLUSTER_NAME";

    public NetworkClusterConditionFieldAutoCompleter() {
        // Building the basic verbs dict.
        mVerbs.put(NETWORK_NAME, NETWORK_NAME);
        mVerbs.put(CLUSTER_NAME, CLUSTER_NAME);

        // Building the autoCompletion dict.
        buildCompletions();

        // Building the types dict.
        getTypeDictionary().put(NETWORK_NAME, String.class);
        getTypeDictionary().put(CLUSTER_NAME, String.class);

        // building the ColumnName dict.
        mColumnNameDict.put(NETWORK_NAME, "network_name");
        mColumnNameDict.put(CLUSTER_NAME, "cluster_name");

        // Building the validation dict.
        buildBasicValidationTable();
    }

    @Override
    public IAutoCompleter getFieldRelationshipAutoCompleter(final String fieldName) {
        final Class<?> clazz = getTypeDictionary().get(fieldName);
        if (clazz == Integer.class) {
            return NumericConditionRelationAutoCompleter.INSTANCE;
        }
        return StringConditionRelationAutoCompleter.INSTANCE;
    }

}
