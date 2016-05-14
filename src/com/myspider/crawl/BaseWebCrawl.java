package com.myspider.crawl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import com.myspider.http.HttpUtils;

import Config.Config;

public abstract class BaseWebCrawl {
	public String Cookie = "ll=\"108258\"; bid=OQ_nLx01kDs; ps=y; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1463133245%2C%22https%3A%2F%2Fwww.douban.com%2F%22%5D; gr_user_id=41f69206-e088-4652-9f9c-74b7096095bd; ap=1; ue=\"1109245765@qq.com\"; dbcl2=\"88174845:ky4Xwz+i4Tk\"; ck=cAKA; push_noty_num=1; push_doumail_num=0; _pk_id.100001.4cf6=128f2ce4775c3860.1462945289.4.1463139036.1463131242.; _pk_ses.100001.4cf6=*; __utma=30149280.2034776171.1462945261.1463129245.1463133245.5; __utmb=30149280.45.10.1463133245; __utmc=30149280; __utmz=30149280.1463133245.5.5.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.14567; __utma=223695111.1591148156.1462945287.1463129245.1463133245.4; __utmb=223695111.0.10.1463133245; __utmc=223695111; __utmz=223695111.1463133245.4.4.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/";

	int Count = 0;
	private String Domin;
	private String startUrl;
	private int Threads;
	protected String currentUser;
	// 用户的阻塞队列
	public LinkedList<String> userQueue = new LinkedList();
	// 用户影评的堵塞队列
	public LinkedList<String> Movie_comments = new LinkedList<>();
	public Set<String> al_urls = new HashSet<>();

	// 具体如何解析字符串和如何保存字符串，提供给用户来编写.
	abstract public boolean Parse(String html, int type,String url);

	abstract public boolean ExsitDB(String url);

	abstract public void Save(LinkedList<String> content,int type);

	public void Start() {
		while (userQueue.size() != 0) {
			String user = userQueue.getFirst();
			System.out.println("当前搜索用户名："+user);
			currentUser = user;
			userQueue.removeFirst();
			if (al_urls.contains(user)) {
				continue;
			} else {
				al_urls.add(user);
			}
			Parse("", Config.USER_URL,user);
			while (Movie_comments.size() != 0) {
				String movie = Movie_comments.getFirst();
				Movie_comments.removeFirst();
				if (al_urls.contains(movie)) {
					continue;
				} else {
					al_urls.add(movie);
				}
				System.out.println("当前此用户的影评："+user);
				HttpUtils httpUtils = new HttpUtils(movie);
				String html = httpUtils.Read(httpUtils.GetConnection("GET", "", Cookie));
				Parse(html, Config.MOVIE_URL,movie);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private void LoadAlready() {

	}

}
