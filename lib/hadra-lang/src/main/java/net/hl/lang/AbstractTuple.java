package net.hl.lang;

import java.util.Objects;

public abstract class AbstractTuple implements Tuple{
    @Override
    public int hashCode() {
        int max=size();
        int h=15*31+max;
        for (int i = 0; i < max; i++) {
            h+= h*31+Objects.hashCode(valueAt(i));
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple that = (Tuple) o;
        int max=size();
        int omax=that.size();
        if(max!=omax){
            return false;
        }
        for (int i = 0; i < max; i++) {
            if(!Objects.equals(valueAt(i),that.valueAt(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("(");
        int max=size();
        for (int i = 0; i < max; i++) {
            if(i>0) {
                sb.append(",");
            }
            sb.append(Objects.toString(valueAt(i)));
        }
        sb.append(")");
        return sb.toString();
    }

    protected void checkRange(int index){
        int size = size();
        if(index<0 || index>=size){
            throw new IllegalArgumentException("Invalid index "+index+" for tuple of size "+ size);
        }
    }
}
