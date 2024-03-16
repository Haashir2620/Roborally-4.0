package dk.dtu.compute.se.pisd.roborally.fileacces;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * A generic type adapter for Gson, which deals with structures, where
 * a statically typed element can have dynamic sub-types. The solution is a
 * simple adaptation of <a href="https://github.com/mperdikeas/json-polymorphism"
 * >https://github.com/mperdikeas/json-polymorphism</a>, which can be used
 * in a generic way. The type parameter <code>E</code> refers to the top of
 * the class hierarchy resp. to the static type, which is dynamically sub-typed
 * in the structure. Note that this solution does not work if instances of
 * E itself need to be serialized (typically E would be abstract).
 *
 * @author Menelaos Perdikeas, https://github.com/mperdikeas
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 * @param <E> The top of the class hierarchy
 */
public class Adapter<E> implements JsonSerializer<E>, JsonDeserializer<E> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public JsonElement serialize(E src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject retValue = new JsonObject();
        String className = src.getClass().getName();
        retValue.addProperty(CLASSNAME, className);
        JsonElement elem = context.serialize(src);
        retValue.add(INSTANCE, elem);
        return retValue;
    }

    @Override
    public E deserialize(JsonElement json, Type typeOfT,
                         JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }
        return context.deserialize(jsonObject.get(INSTANCE), klass);
    }
}
