/*
 * This file is generated by jOOQ.
 */
package edu.java.repository.jooq.generated.tables;


import edu.java.repository.jooq.generated.DefaultSchema;
import edu.java.repository.jooq.generated.Keys;
import edu.java.repository.jooq.generated.tables.records.UserChatRecord;

import java.util.function.Function;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function1;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row1;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserChat extends TableImpl<UserChatRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>USER_CHAT</code>
     */
    public static final UserChat USER_CHAT = new UserChat();

    /**
     * The class holding records for this type
     */
    @Override
    @NotNull
    public Class<UserChatRecord> getRecordType() {
        return UserChatRecord.class;
    }

    /**
     * The column <code>USER_CHAT.ID</code>.
     */
    public final TableField<UserChatRecord, Long> ID = createField(DSL.name("ID"), SQLDataType.BIGINT.nullable(false), this, "");

    private UserChat(Name alias, Table<UserChatRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserChat(Name alias, Table<UserChatRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>USER_CHAT</code> table reference
     */
    public UserChat(String alias) {
        this(DSL.name(alias), USER_CHAT);
    }

    /**
     * Create an aliased <code>USER_CHAT</code> table reference
     */
    public UserChat(Name alias) {
        this(alias, USER_CHAT);
    }

    /**
     * Create a <code>USER_CHAT</code> table reference
     */
    public UserChat() {
        this(DSL.name("USER_CHAT"), null);
    }

    public <O extends Record> UserChat(Table<O> child, ForeignKey<O, UserChatRecord> key) {
        super(child, key, USER_CHAT);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    @NotNull
    public UniqueKey<UserChatRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_B;
    }

    @Override
    @NotNull
    public UserChat as(String alias) {
        return new UserChat(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public UserChat as(Name alias) {
        return new UserChat(alias, this);
    }

    @Override
    @NotNull
    public UserChat as(Table<?> alias) {
        return new UserChat(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public UserChat rename(String name) {
        return new UserChat(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public UserChat rename(Name name) {
        return new UserChat(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public UserChat rename(Table<?> name) {
        return new UserChat(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row1 type methods
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row1<Long> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function1<? super Long, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function1<? super Long, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
