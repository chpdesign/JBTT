package tracker.torrents;

import play.Logger;
import tracker.DatabaseFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Tag {
	private Integer id;
	private Integer groupId;
	private String key;
	private String title;

	public Tag() { }

	public Tag(String title, String key, Integer groupId, Integer id) {
		this.title = title;
		this.key = key;
		this.groupId = groupId;
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = this.groupId;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static Tag byId(Integer id) throws Throwable {
		if (id < 1) {
			return null;
		}

		String condition = String.format("`id` = %d", id);
		return byCondition(condition);
	}

	public static Tag byKey(String key) throws Throwable {
		String condition = String.format("`key` = \"%s\"", key);
		return byCondition(condition);
	}

	public static Tag byCondition(String condition) throws Throwable {
		List<Tag> tags = byConditionMultiple(condition);
		if (tags.size() < 1) {
			return null;
		}

		return tags.get(0);
	}

	public static List<Tag> byConditionMultiple(String condition) throws Throwable {
		Logger.debug("Load tags by condition " + condition);

		List<Tag> tags = new ArrayList<Tag>();

		String query = "SELECT `id`, `group_id`, `key`, `title` FROM `tags` WHERE " + condition;
		PreparedStatement statement = DatabaseFactory.getInstance().getConnection().prepareStatement(query);

		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			Tag tag = new Tag();
			tag.setId(resultSet.getInt("id"));
			tag.setGroupId(resultSet.getInt("group_id"));
			tag.setKey(resultSet.getString("key"));
			tag.setTitle(resultSet.getString("title"));

			tags.add(tag);
		}

		DatabaseFactory.close(statement.getConnection());

		return tags;
	}
}
