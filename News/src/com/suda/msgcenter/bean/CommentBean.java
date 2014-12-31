package com.suda.msgcenter.bean;

import java.io.Serializable;

public class CommentBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	 /*
     * 评论内容的ID
     */
    private String id;
    
    /*
     * 话题ID
     */
    private String topicId;
    
    /*
     * 评论者昵称
     */
    private String reviewer;

    /*
     * 评论时间
     */
    private String createTime;
    /*
     * 该条评论的状态
     */
    private String status;
    /*
     * 评论的内容
     */
    private String content;
    
    /*
     * 评论的支持数
     */
    private String support;
    /*
     * 评论的反对数
     */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSupport() {
		return support;
	}
	public void setSupport(String support) {
		this.support = support;
	}

    
    
    
}
