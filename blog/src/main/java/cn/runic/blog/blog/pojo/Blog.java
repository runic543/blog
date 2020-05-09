package cn.runic.blog.blog.pojo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity //  @Entity ：表明是一个实体类
@Table(name = "t_blog")  //@Table ：对应的数据表名
public class Blog {

    @Id  //表示这个属性是 主键
    @GeneratedValue //主键生成策略
    private Long id;  // 博客 ID


    private String title;                 // 标题

    /**
     *  @Lob 表这个数据是 LongText 数据，即长文本内容数据
     *  @Basic 往往是配合 Lob 的，只有查询的时候才会去调用 长文本的资源。防止内存耗费
     */
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String content;               // 内容

    private String firstPicture;          // 首图
    private String flag;                  // 标记
    private Integer views;                // 浏览数
    private boolean appreciation;         // 赞赏
    private boolean shareStatement;       // 版权开启
    private boolean commentabled;         //  评论开启
    private boolean published;            // 发布          1 / true 即发布状态， 0 / false 即保存状态
    private boolean recommend;            //  是否推荐


    /*
      一般我们在Java中，类型转换需要如下操作：

                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd")

                Date date=new Date();

                String newDate=sf.formmat(date);

                显得很麻烦；下面这个标签作用就跟上面一样；

        @Temporal标签的作用如下：

        在一个类里面如果有Date类型的属性，可以用注解@Temporal进行类型的转换；

               (1) @Temporal(TemporalType.DATE) 会得到形如'yyyy-MM-dd' 格式的日期。

               (2) @Temporal(TemporalType.TIME) 会得到形如'HH:MM:SS' 格式的日期。

               (3) @Temporal(TemporalType.TIMESTAMP) 会得到形如'yyyy-MM-dd HH:MM:SS' 格式的日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;              // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;             //  更新时间





    @ManyToOne  //  Hibernate 多表关联de 注解，表示 Blog 是多 ，Type 是 1
                //  使用多的一端 作为 “关系维护端”
    private Type type;


    // 和 Tag 类进行多对多 关系映射 ，且作为 关系维护端
    @ManyToMany(cascade = {CascadeType.PERSIST})  // cascade ... 表示级联新增
    private List<Tag> tags = new ArrayList<>();   //  作用 : 如果新增加一个 Blog ，那么也会新增一个 Tag 然后保存至数据库


    @ManyToOne   // ManyToOne, 多方默认就是 关系维护端
    private User user;


    @OneToMany(mappedBy = "blog" )
    //关系被维护端   通过 Comment 中的 blog 属性进行关联
    private List<Comment> comments = new ArrayList<>();



















    @Transient //表这个属性不进数据库，只是一个属性而已
    private String tagIds;  //这个属性是专门为 前端 blog-input.html 中
                            // 要传给后端的 tagIds 设置的
                            // 主要是用来在 BlogController 中的 post() 方法获取 tagIds

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }


    /**
     *  这个方法是为了 编辑博客 这个功能写的
     *
     *  在    blog-input.html 中，<input type="hidden" name="tagIds" th:value="*{tagIds}" >
     *  后端要把 tagIds  发送给前端，但是由于 tagIds 是一个集合
     *  因此才写这个 init() 处理
    *
     */
    public void init() {
        this.tagIds = tagsToIds(this.getTags());
    }

    //最终返回的格式   1,2,3
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }

















    /*
         新增的一个属性，博客描述
     */
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }















    public Blog() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public boolean isAppreciation() {
        return appreciation;
    }

    public void setAppreciation(boolean appreciation) {
        this.appreciation = appreciation;
    }

    public boolean isShareStatement() {
        return shareStatement;
    }

    public void setShareStatement(boolean shareStatement) {
        this.shareStatement = shareStatement;
    }

    public boolean isCommentabled() {
        return commentabled;
    }

    public void setCommentabled(boolean commentabled) {
        this.commentabled = commentabled;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", flag='" + flag + '\'' +
                ", views=" + views +
                ", appreciation=" + appreciation +
                ", shareStatement=" + shareStatement +
                ", commentabled=" + commentabled +
                ", published=" + published +
                ", recommend=" + recommend +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", type=" + type +
                ", tags=" + tags +
                ", user=" + user +
                ", comments=" + comments +
                ", tagIds='" + tagIds + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
