package dev.masonak.redis.core.value;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class ListValue implements Value {

    private Deque<byte[]> deque = new ArrayDeque<>();

    @Override
    public ValueType type() {
        return ValueType.List;
    }

    @Override
    public byte[] bytes() {
        return null;
    }

    public void addFirst(String input) {
        deque.addFirst(input.getBytes(StandardCharsets.UTF_8));
    }

    public void addLast(String input) {
        deque.addLast(input.getBytes(StandardCharsets.UTF_8));
    }

    public String pollFirst() { return new String(deque.pollFirst(), StandardCharsets.UTF_8); }

    public Integer size() {
        return deque.size();
    }

    public List<String> toStringList() {
        ListValue temp = new ListValue();
        temp.setDeque(cloneDeque());
        List<String> result = new ArrayList<>();
        int tempSize = temp.size();
        for (int i = 0; i < tempSize; i++) {
            result.add(temp.pollFirst());
        }
        return result;
    }

    public ListValue sublist(int start, int stop) {
        ListValue result = new ListValue();
        result.setDeque(cloneDeque());
        for (int i = 0; i < start; i++) {
            result.pollFirst();
        }
        Deque<byte[]> temp = new ArrayDeque<>();
        for (int i = start; i <= stop; i++) {
            temp.addLast(result.getDeque().pollFirst());
        }
        result.setDeque(temp);
        return result;
    }

    private void setDeque(Deque<byte[]> bytes) {
        this.deque = bytes;
    }

    public Deque<byte[]> getDeque() {
        return deque;
    }

    private Deque<byte[]> cloneDeque() {
        Deque<byte[]> result = new ArrayDeque<>();
        Iterator<byte[]> itr = deque.iterator();
        while (itr.hasNext()) {
            result.addLast(itr.next());
        }
        return result;
    }

}