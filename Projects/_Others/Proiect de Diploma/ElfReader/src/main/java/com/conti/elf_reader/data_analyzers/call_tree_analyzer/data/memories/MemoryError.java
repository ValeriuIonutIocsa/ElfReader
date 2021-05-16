package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories;

public class MemoryError {

    private final String target;
    private final String callerProperty;
    private final String calledProperty;
    private final ErrorType errorType;

    public MemoryError(
            String target, String callerProperty, String calledProperty, ErrorType errorType) {

        this.target = target;
        this.calledProperty = calledProperty;
        this.callerProperty = callerProperty;
        this.errorType = errorType;
    }

    public ErrorType searchForErrorType(String callerMemoryName, String calledMemoryName) {

        if (this.callerProperty != null && this.callerProperty.equals(callerMemoryName)
                && this.calledProperty != null && this.calledProperty.equals(calledMemoryName)) {
            return errorType;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof MemoryError))
            return false;

        MemoryError other = (MemoryError) o;
        return callerProperty != null && callerProperty.equals(other.callerProperty)
                && calledProperty != null && calledProperty.equals(other.calledProperty);
    }

    public String getTarget() {
        return target;
    }

    public String getCallerProperty() {
        return callerProperty;
    }

    public String getCalledProperty() {
        return calledProperty;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
