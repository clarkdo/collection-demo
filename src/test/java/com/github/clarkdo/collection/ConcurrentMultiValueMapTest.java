package com.github.clarkdo.collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created by clark on 2016/5/9.
 */
public class ConcurrentMultiValueMapTest {

    AbstractMultiValueMap<String, String> map = null;

    @Before
    public void setUp() throws Exception {
        map = new ConcurrentMultiValueMap<>();
    }

    @After
    public void tearDown() throws Exception {
        map.clear();
        map = null;
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testPutSingle() throws Exception {
        map.putSingle("Author", "Jason");
        map.putSingle("Author", "Clark");
        assertEquals("Clark", map.getFirst("Author"));
        map.add("Author", "Mr.X");
    }

    @Test
    public void testAddAndGet() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(Arrays.asList("Jason", "Clark"), map.get("Members"));
    }

    @Test
    public void testGetFirst() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals("Jason", map.getFirst("Members"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, map.size());
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(1, map.size());
        map.add("Author", "Clark");
        assertEquals(2, map.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, map.isEmpty());
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(false, map.isEmpty());
        map.clear();
        assertEquals(true, map.isEmpty());
    }

    @Test
    public void testContainsKey() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(true, map.containsKey("Members"));
        assertEquals(false, map.containsKey("Author"));
    }

    @Test
    public void testContainsValue() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(true, map.containsValue(Arrays.asList("Jason","Clark")));
        assertEquals(false, map.containsValue(Arrays.asList("Jason","Clark","Ray")));
        assertEquals(false, map.containsValue("Clark"));
    }

    @Test
    public void testPut() throws Exception {
        map.put("Members", Collections.singletonList("Clark"));
        map.put("Members", Collections.singletonList("Jason"));
        map.put("Author", Collections.singletonList("Clark"));
        assertEquals(false, map.containsValue(Arrays.asList("Jason","Clark")));
        assertEquals(Collections.singletonList("Jason"), map.get("Members"));
        assertEquals(Collections.singletonList("Clark"), map.get("Author"));
    }

    @Test
    public void testRemove() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(true, map.containsValue(Arrays.asList("Jason","Clark")));
        map.remove("Members");
        assertEquals(true, map.isEmpty());

    }

    @Test
    public void testRemoveValue() throws Exception {
        map.add("Members", "Jason");
        map.add("Members", "Clark");
        assertEquals(true, map.containsValue(Arrays.asList("Jason","Clark")));
        map.removeValue("Members", "Jason");
        assertEquals(Collections.singletonList("Clark"), map.get("Members"));

    }

    @Test
    public void testPutAll() throws Exception {
        MultivaluedMap<String, String> replica = new ConcurrentMultiValueMap<>();
        replica.put("Members", Arrays.asList("Jason"));
        replica.add("Members", "Clark");
        replica.put("Author", Arrays.asList("Clark"));
        map.putAll(replica);
        assertEquals(Arrays.asList("Jason", "Clark"), map.get("Members"));
        assertEquals(Arrays.asList("Clark"), map.get("Author"));
    }
}