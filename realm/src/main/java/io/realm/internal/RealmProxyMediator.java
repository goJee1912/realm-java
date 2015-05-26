/*
 * Copyright 2015 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.internal;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;

/**
 * Superclass for the RealmProxyMediator class'. This class contain all static methods introduced by the
 * annotation processor as part of the RealmProxy classes.
 *
 * Classes extending this class act as binders between the static methods inside each RealmProxy and the code
 * at runtime. We cannot rely on using reflection as the RealmProxies are generated by the annotation processor
 * before ProGuard is run. So after ProGuard has run there is a mismatch between the name of the RealmProxy and
 * the original RealmObject class.
 */
public abstract class RealmProxyMediator {

    /**
     * Creates the backing table in Realm for the given model class.
     *
     * @param clazz         RealmObject model class to create backing table for.
     * @param transaction   Read transaction for the Realm to create table in.
     */
    public abstract Table createTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction);

    /**
     * Validate the backing table in Realm for the given model class.
     *
     * @param clazz         RealmObject model class to validate.
     * @param transaction   Read transaction for the Realm to validate against.
     */
    public abstract void validateTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction);

    /**
     * Returns a map of non-obfuscated object field names to their internal realm name.
     *
     * @param clazz  RealmObject model class reference.
     * @return The simple name of an model class (before it has been obfuscated)
     */
    public abstract List<String> getFieldNames(Class<? extends RealmObject> clazz);

    /**
     * Returns name that Realm should use for all it's internal tables. This is normally the unobfuscated named of a
     * class.
     *
     * @param clazz  RealmObject model class reference.
     * @return The simple name of an model class (before it has been obfuscated)
     *
     * @throws java.lang.NullPointerException if null is given as argument.
     */
    public abstract String getTableName(Class<? extends RealmObject> clazz);

    /**
     * Creates a new instance of an RealmProxy for the given model class.
     *
     * @param clazz RealmObject to create RealmProxy for.
     * @return Created RealmProxy object.
     */
    public abstract <E extends RealmObject> E newInstance(Class<E> clazz);

    /**
     * Returns the list of model classes that Realm supports in this application.
     *
     * @return List of class references to model classes. Empty list if no models are supported.
     */
    public abstract List<Class<? extends RealmObject>> getModelClasses();

    /**
     * Returns a map of the column indices for all Realm fields in the model class.
     *
     * @return Map from field name to column indices for all Realm fields in the model class.
     */
    public abstract  Map<String, Long> getColumnIndices(Class<? extends RealmObject> clazz);

    /**
     * Copy a non-manged RealmObject or a RealmObject from another Realm to this Realm. After being
     * copied any changes to the original object will not be persisted.
     *
     * @param object Object to copy properties from.
     * @param update True if object has a primary key and should try to update already existing data, false otherwise.
     * @param cache Cache for mapping between standalone objects and their RealmProxy representation.
     * @return Managed Realm object.
     */
    public abstract <E extends RealmObject> E copyOrUpdate(Realm realm, E object, boolean update, Map<RealmObject, RealmObjectProxy> cache);

    /**
     * Creates or updates a RealmObject using the provided JSON data.
     *
     * @param clazz     Type of RealmObject
     * @param realm     Reference to Realm where to create the object.
     * @param json      JSON data
     * @param update    True if Realm should try to update a existing object. This requires that the model has a @PrimaryKey
     * @return RealmObject that has been created or updated.
     * @throws JSONException If the JSON mapping doesn't match the expected class.
     */
    public abstract <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update) throws JSONException;

    /**
     * Creates new RealmObjects based on a JSON input stream.
     *
     * @param clazz     Type of RealmObject
     * @param realm     Reference to Realm where to create the object.
     * @param reader    Reference to the InputStream containg the JSON data.
     * @return The created RealmObject
     * @throws IOException if an error occurs with the inputstream.
     */
    public abstract <E extends RealmObject> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader) throws java.io.IOException;

    protected static void checkClass(Class<? extends RealmObject> clazz) {
        if (clazz == null) {
            throw new NullPointerException("A class extending RealmObject must be provided");
        }
    }

    protected static RealmException getMissingProxyClassException(Class<? extends RealmObject> clazz) {
        return new RealmException(clazz + " is not part of the schema for this Realm.");
    }
}
