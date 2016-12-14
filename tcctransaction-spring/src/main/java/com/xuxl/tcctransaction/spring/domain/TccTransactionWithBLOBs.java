package com.xuxl.tcctransaction.spring.domain;

import java.io.Serializable;
import java.util.Arrays;

public class TccTransactionWithBLOBs extends TccTransaction implements Serializable {
    private byte[] globalTxId;

    private byte[] branchQualifier;

    private byte[] content;

    private static final long serialVersionUID = 1L;

    public byte[] getGlobalTxId() {
        return globalTxId;
    }

    public void setGlobalTxId(byte[] globalTxId) {
        this.globalTxId = globalTxId;
    }

    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    public void setBranchQualifier(byte[] branchQualifier) {
        this.branchQualifier = branchQualifier;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", globalTxId=").append(globalTxId);
        sb.append(", branchQualifier=").append(branchQualifier);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        sb.append(", from super class ");
        sb.append(super.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TccTransactionWithBLOBs other = (TccTransactionWithBLOBs) that;
        return (this.getTransactionId() == null ? other.getTransactionId() == null : this.getTransactionId().equals(other.getTransactionId()))
            && (this.getDomain() == null ? other.getDomain() == null : this.getDomain().equals(other.getDomain()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getTransactionType() == null ? other.getTransactionType() == null : this.getTransactionType().equals(other.getTransactionType()))
            && (this.getRetriedCount() == null ? other.getRetriedCount() == null : this.getRetriedCount().equals(other.getRetriedCount()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getLastUpdateTime() == null ? other.getLastUpdateTime() == null : this.getLastUpdateTime().equals(other.getLastUpdateTime()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (Arrays.equals(this.getGlobalTxId(), other.getGlobalTxId()))
            && (Arrays.equals(this.getBranchQualifier(), other.getBranchQualifier()))
            && (Arrays.equals(this.getContent(), other.getContent()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTransactionId() == null) ? 0 : getTransactionId().hashCode());
        result = prime * result + ((getDomain() == null) ? 0 : getDomain().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTransactionType() == null) ? 0 : getTransactionType().hashCode());
        result = prime * result + ((getRetriedCount() == null) ? 0 : getRetriedCount().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getLastUpdateTime() == null) ? 0 : getLastUpdateTime().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + (Arrays.hashCode(getGlobalTxId()));
        result = prime * result + (Arrays.hashCode(getBranchQualifier()));
        result = prime * result + (Arrays.hashCode(getContent()));
        return result;
    }
}