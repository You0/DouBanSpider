package com.myspider.crawl;

import java.net.CookieHandler;
import java.security.KeyStore.PrivateKeyEntry;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.relation.RelationServiceNotRegisteredException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.myspider.databaseUtils.DatabaseUtils;
import com.myspider.http.HttpUtils;

import Config.Config;

public class Douban extends BaseWebCrawl {

	@Override
	public boolean Parse(String html, int type, String url) {
		LinkedList<String> SaveContent = new LinkedList<>();
		switch (type) {
		case Config.USER_URL: {
			SaveContent.clear();
			String username = url;
			String contacts = "https://www.douban.com/people/" + username + "/contacts";
			String rev_contacts = "https://www.douban.com/people/" + username + "/rev_contacts";
			String movie = "https://movie.douban.com/people/" + username
					+ "/collect?start=1&sort=time&rating=all&filter=all&mode=grid";

			// System.out.println(movie);
			SaveContent.add(username);

			// 解析关注
			HttpUtils httpUtils = new HttpUtils(contacts);
			String html1 = httpUtils.Read(httpUtils.GetConnection("GET", "", Cookie));
			LinkedList<String> linkedList = contacts(html1);
			userQueue.addAll(linkedList);
			String contacts1 = linkedList.toString();

			// 解析被关注
			httpUtils = new HttpUtils(rev_contacts);
			html1 = httpUtils.Read(httpUtils.GetConnection("GET", "", Cookie));
			linkedList = contacts(html1);
			userQueue.addAll(linkedList);
			String rev_contacts1 = linkedList.toString();

			SaveContent.add(contacts1);
			SaveContent.add(rev_contacts1);
			// System.out.println(rev_contacts1);
			httpUtils = new HttpUtils(movie);
			html1 = httpUtils.Read(httpUtils.GetConnection("GET", "", Cookie));
			int count = getCountMovie(html1);
			int page = count / 15;
			if(page>33){
				page = 30;
			}
			for (int i = 1; i <= page; i++) {
				// System.out.println(i);
				Movie_comments.add("https://movie.douban.com/people/" + username + "/collect?start=" + i * 15
						+ "&sort=time&rating=all&filter=all&mode=grid");
			}
			Save(SaveContent, type);

			break;
		}

		case Config.MOVIE_URL: {

			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementsByClass("item");
			for (Element e : elements) {
				SaveContent.clear();
				String item = e.toString();
				doc = Jsoup.parse(item);
				Elements title = doc.getElementsByClass("title");
				// System.out.println(title.text());
				SaveContent.add(title.text());
				Elements intro = doc.getElementsByClass("intro");
				// System.out.println(intro.text());
				SaveContent.add(intro.text());
				Elements comment = doc.getElementsByClass("comment");
				// System.out.println(comment.text());
				SaveContent.add(comment.text());
				SaveContent.add(currentUser);
				SaveContent.add(String.valueOf(item.charAt(item.indexOf("rating") + 6)));
				 System.out.println(SaveContent.toString());
				Save(SaveContent, type);
			}
			break;
		}

		}

		return false;
	}

	@Override
	public boolean ExsitDB(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Save(LinkedList<String> content, int type) {
		switch (type) {
		case Config.USER_URL: {
			System.out.println("开始保存用户关系");
			Object[] objects = content.toArray();
			String sql = "SELECT COUNT(*) FROM user WHERE uid=?";
			Connection connection = DatabaseUtils.getConnection_Nopool();
			int exist = DatabaseUtils.queryInt(sql, connection, objects[0]);
			if (exist > 0) {
				System.out.println("已经存在~跳过咯");
				// 没有close掉connect导致数据库连接池堵塞了线程。。。
				DatabaseUtils.closeConnection(connection);
			} else {
				sql = "insert into user (uid,contacts,rev_contacts) values (?,?,?)";
				connection = DatabaseUtils.getConnection_Nopool();
				DatabaseUtils.update(sql, connection, objects);
				DatabaseUtils.closeConnection(connection);
			}
			break;
		}

		case Config.MOVIE_URL: {

			
			Object[] objects = content.toArray();
			String sql = "SELECT COUNT(*) FROM movie WHERE uid=? AND comments=? AND movie_name=?";
			Connection connection = DatabaseUtils.getConnection_Nopool();
			int exist = DatabaseUtils.queryInt(sql, connection, objects[3], objects[2], objects[0]);
			if (exist > 0) {
				System.out.println("已经存在~跳过咯");
				// 没有close掉connect导致数据库连接池堵塞了线程。。。
				DatabaseUtils.closeConnection(connection);
			} else {
				sql = "insert into movie (movie_name,type,comments,uid,rating) values (?,?,?,?,?)";
				connection = DatabaseUtils.getConnection_Nopool();
				DatabaseUtils.update(sql, connection, content.toArray());
				DatabaseUtils.closeConnection(connection);
			}
			break;
		}

		}

	}

	public int getCountMovie(String html) {

		Document doc = Jsoup.parse(html);
		int value;
		Elements elements = doc.getElementsByClass("subject-num");
		for (Element e : elements) {
			String eString = e.text().toString();
			try {
				value = Integer.valueOf(eString.substring(7, eString.length()));
			} catch (Exception e2) {
				return 0;
			}
			// System.out.println(value);
			return value;
		}
		return 0;
	}

	public LinkedList<String> contacts(String html) {
		LinkedList<String> linkedList = new LinkedList<>();
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByClass("obu");
		for (Element e : elements) {
			doc = Jsoup.parse(e.toString());
			Elements urls = doc.getElementsByAttribute("href");
			int i = 1;
			for (Element e1 : urls) {
				if ((i & 0x1) == 1) {
					String linkHref = e1.attr("href");
					linkedList.add(linkHref.substring(30, linkHref.length() - 1));
					// System.out.println(linkHref);
				}
				i++;
			}

		}
		return linkedList;

	}

	public static void main(String[] args) {
		Douban douban = new Douban();
		douban.userQueue.add("77212612");
		douban.Start();
	}
}
