package me.justapie.cakedj.database.collections;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import me.justapie.cakedj.Constants;
import me.justapie.cakedj.database.models.GuildModel;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import javax.annotation.Nullable;

public class GuildCollection {
    private static MongoCollection<Document> collection;

    public static void init(MongoDatabase database) {
        collection = database.getCollection(Constants.guildCollection);
    }

    public static void createGuildConfig(Guild guild) {
        if (collection.find(Filters.eq(new Document(Constants.guildIDKey, guild.getId()))).first() != null)
            return;
        collection.insertOne(Constants.getDefaultGuildSetting(guild));
    }

    @Nullable
    public static GuildModel getGuildConfig(Guild guild) {
        Document document = collection.find(
                Filters.eq(Constants.guildIDKey, guild.getId())
        ).first();
        if (document == null)
            return null;
        return Constants.parseGuildSetting(document);
    }

    public static void modifyGuildConfig(Guild guild, String key, Object value) {
        Document document = collection.find(
                Filters.eq(Constants.guildIDKey, guild.getId())
        ).first();
        if (document == null)
            return;
        document = new Document("$set", new Document(key, value));
        collection.findOneAndUpdate(Filters.eq(Constants.guildIDKey, guild.getId()), document);
    }

    public static void deleteGuildConfig(Guild guild) {
        collection.findOneAndDelete(Filters.eq(new Document(Constants.guildIDKey, guild.getId())));
    }
}
