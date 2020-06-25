package net.hl.lang;

public final class IntRef implements Ref<Integer>{
    private int intValue;

    public IntRef(int intValue) {
        this.intValue = intValue;
    }

    public int getInt() {
        return intValue;
    }

    public void setInt(int intValue) {
        this.intValue = intValue;
    }

    public void inc(){
        this.intValue++;
    }

    public void dec(){
        this.intValue--;
    }

    @Override
    public Integer get() {
        return intValue;
    }

    @Override
    public void set(Integer integer) {
        this.intValue=integer.intValue();
    }

    @Override
    public String toString() {
        return String.valueOf(intValue);
    }
}
