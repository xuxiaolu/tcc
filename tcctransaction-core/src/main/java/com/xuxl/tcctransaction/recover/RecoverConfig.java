package com.xuxl.tcctransaction.recover;

public interface RecoverConfig {

    public int getMaxRetryCount();

    public int getRecoverDuration();

    public String getCronExpression();
}
