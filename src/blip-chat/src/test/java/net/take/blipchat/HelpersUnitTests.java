package net.take.blipchat;

import net.take.blipchat.utils.Helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class HelpersUnitTests {

    @Test
    public void forAValidHashMapShouldSerialize() throws Exception {

        String expectedJson = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        HashMap<String, String> validHash = new HashMap<>();
        validHash.put("key1", "value1");
        validHash.put("key2", "value2");

        String jsonResult = Helpers.serializeExtras(validHash);

        assertEquals(expectedJson, jsonResult);
    }

    @Test
    public void forAValidHashMapWithNullFieldsShouldSerializeWithoutNull() throws Exception {

        String expectedJson = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        HashMap<String, String> validHash = new HashMap<>();
        validHash.put("key1", "value1");
        validHash.put("key2", "value2");
        validHash.put("key3", null);

        String jsonResult = Helpers.serializeExtras(validHash);

        assertEquals(expectedJson, jsonResult);
    }

    @Test
    public void forANullHashMapShouldNotSerialize() throws Exception {

        HashMap<String, String> validHash = null;

        String jsonResult = Helpers.serializeExtras(validHash);

        assertNull(jsonResult);
    }

    @Test
    public void forAEmptyHashMapShouldNotSerialize() throws Exception {

        HashMap<String, String> validHash = new HashMap<>();

        String jsonResult = Helpers.serializeExtras(validHash);

        assertNull(jsonResult);
    }
}
