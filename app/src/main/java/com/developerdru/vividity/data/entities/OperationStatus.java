package com.developerdru.vividity.data.entities;

public class OperationStatus {

    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_ERROR = 2;
    private static final int STATUS_COMPLETE = 3;

    private int status;
    private String Extra;
    private int completionPercentage;

    private OperationStatus(int status, String Extra, int completionPercentage) {
        this.status = status;
        this.Extra = Extra;
        this.completionPercentage = completionPercentage;
    }

    public boolean isComplete() {
        return (status == STATUS_COMPLETE);
    }

    public boolean isErroneous() {
        return (status == STATUS_ERROR);
    }

    public boolean isInProgress() {
        return (status == STATUS_IN_PROGRESS);
    }

    public String getExtra() {
        return Extra;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public static OperationStatus getInProgresStatus(int progressPercentage) {
        return new OperationStatus(STATUS_IN_PROGRESS, null, progressPercentage);
    }

    public static OperationStatus getCompletedStatus() {
        return new OperationStatus(STATUS_COMPLETE, null, 0);
    }

    public static OperationStatus getCompletedStatus(String extra) {
        return new OperationStatus(STATUS_COMPLETE, extra, 0);
    }

    public static OperationStatus getErrorStatus(String extra) {
        return new OperationStatus(STATUS_ERROR, extra, -1);
    }
}
