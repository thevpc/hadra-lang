package net.hl.lang;

public final class BooleanRef implements Ref<Boolean>{
    private boolean booleanValue;

    public BooleanRef(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean getBoolean() {
        return booleanValue;
    }

    public void setBoolean(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Override
    public Boolean get() {
        return booleanValue;
    }

    @Override
    public void set(Boolean booleanValue) {
        this.booleanValue =booleanValue.booleanValue();
    }

    @Override
    public String toString() {
        return String.valueOf(booleanValue);
    }
}
