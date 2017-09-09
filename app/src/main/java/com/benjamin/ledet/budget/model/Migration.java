package com.benjamin.ledet.budget.model;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1){
            schema.create("Month")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("month", Integer.class)
                    .addField("year", Integer.class, FieldAttribute.INDEXED)
                    .addRealmListField("amounts",schema.get("Amount"));

            schema.create("Category")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("label", String.class)
                    .addField("isIncome", Boolean.class)
                    .addRealmListField("amounts",schema.get("Amount"));

            schema.create("Amount")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("category", schema.get("Category"))
                    .addField("day", Integer.class)
                    .addRealmObjectField("month", schema.get("Month"))
                    .addField("label", String.class)
                    .addField("amount", Double.class);

            schema.create("User")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("email", String.class)
                    .addField("givenName", String.class)
                    .addField("familyName", String.class)
                    .addField("photoUrl", String.class);

            oldVersion++;
        }

        if (oldVersion == 2){
            schema.create("User")
                    .addField("id", Long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("email", String.class)
                    .addField("givenName", String.class)
                    .addField("familyName", String.class)
                    .addField("photoUrl", String.class);

            oldVersion++;
        }

        if (oldVersion == 3){
            schema.get("Category").removeField("amounts");
            schema.get("Month").removeField("amounts");

            oldVersion++;
        }


        if (oldVersion == 4) {
            schema.get("Category")
                    .addRealmListField("amounts",schema.get("Amount"))
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            for (DynamicRealmObject amount : realm.where("Amount").equalTo("category.id",obj.getInt("id")).findAll())
                                obj.getList("amounts").add(amount);
                        }
                    });

            schema.create("AutomaticAmount")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("category", schema.get("Category"))
                    .addField("day", int.class)
                    .addField("label", String.class)
                    .addField("amount", double.class);

            schema.get("Category")
                    .addRealmListField("automaticAmounts",schema.get("AutomaticAmount"));

            oldVersion++;
        }

        if (oldVersion == 5) {

            schema.get("AutomaticAmount")
                    .addRealmObjectField("monthOfCreation", schema.get("Month"))
                    .addField("nextMonth",boolean.class);

            oldVersion++;
        }

        if (oldVersion == 6) {

            schema.get("Category")
                    .addField("isArchived",boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("isArchived",false);
                        }
                    });

            oldVersion++;
        }

        if (oldVersion == 7) {

            schema.get("Category")
                    .addField("budget",double.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setDouble("budget",0);
                        }
                    });

            oldVersion++;

        }

    }
}
