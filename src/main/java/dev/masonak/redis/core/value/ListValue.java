package dev.masonak.redis.core.value;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class ListValue implements Value {

    private Deque<byte[]> deque = new ConcurrentLinkedDeque<>();

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

    public String pollFirst() {
        byte[] bytes = deque.pollFirst();
        if (bytes == null)
            return "";
        return new String(bytes, StandardCharsets.UTF_8);
    }

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
        Deque<byte[]> temp = new ConcurrentLinkedDeque<>();
        for (int i = start; i <= stop; i++) {
            byte[] val = result.getDeque().pollFirst();
            if (val != null)
                temp.addLast(val);
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
        Deque<byte[]> result = new ConcurrentLinkedDeque<>();
        Iterator<byte[]> itr = deque.iterator();
        while (itr.hasNext()) {
            result.addLast(itr.next());
        }
        return result;
    }

}