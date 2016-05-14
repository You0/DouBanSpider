# DouBanSpider

由于豆瓣有防爬虫机制，所以多线程的设计无什么意义。每次爬完后暂停1秒
全部单线程实现.数据库链接的操作类，之前用了数据库连接池，
这次直接copy过来的。所以没有删，但是加了一个没用连接池的方法。调用的那个方法。

豆瓣爬虫简单分析：

用户的个人主页：
	https://www.douban.com/people/xxxxx/

用户的关注：
	https://www.douban.com/people/xxxxx/contacts

用户被关注：
	https://www.douban.com/people/xxxxx/rev_contacts

用户看过的电影：
	https://movie.douban.com/people/xxxxx/collect?start=1&sort=time&rating=all&filter=all&mode=grid

用户的评论：
	https://www.douban.com/people/xxxxx/reviews
用户评论的下一页：
	https://movie.douban.com/people/xxxxx/collect?start=1&sort=time&rating=all&filter=all&mode=grid

Blockingqueue设计：
	2个blockingqueue：
		1、用户的个人主页
		2、电影影评

数据库的设计:
	user用户信息：uid，关注的人contacts,他关注的人rev_contacts
	movie电影信息:电影名字movie_name,演员及其类型:type,评论:comments,此条评论的uid,用户打分rating


mysql 创建表：

create database douban;

use douban;

create table user
(
id int not null auto_increment,	
uid text not null,
contacts text not null,
rev_contacts text not null,
primary key(id)
);


create table movie
(
id int not null auto_increment,	
movie_name text not null,
type text not null,
comments text not null,
rating text not null,
uid text not null,
primary key(id)
);



