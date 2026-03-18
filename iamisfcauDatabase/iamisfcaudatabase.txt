USE iamisfcaudatabase;

-- 为了可重复执行（开发阶段），先按依赖顺序删除
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS community_post_reactions;
DROP TABLE IF EXISTS community_comments;
DROP TABLE IF EXISTS community_posts;

DROP TABLE IF EXISTS activity_attendance;
DROP TABLE IF EXISTS user_activity_rel;
DROP TABLE IF EXISTS activity_leave_applications;
DROP TABLE IF EXISTS activity_create_applications;
DROP TABLE IF EXISTS profile_change_applications;

DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS notices;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- ============ 1) users 用户表 ============
CREATE TABLE users (
  id            VARCHAR(20)  NOT NULL,
  name          VARCHAR(50)  NOT NULL,
  role          VARCHAR(20)  NOT NULL,      -- 管理员/教师/辅导员/学生
  password      VARCHAR(100) NOT NULL,
  gender        VARCHAR(10)  NULL,
  avatar        VARCHAR(255) NULL,
  department    VARCHAR(50)  NULL,          -- 你 mock 里 users 用 department
  college       VARCHAR(50)  NULL,          -- appState.user 用 college
  phone         VARCHAR(20)  NULL,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 2) notices 通知表（tags 融合进本表） ============
CREATE TABLE notices (
  id          INT          NOT NULL,
  title       VARCHAR(200) NOT NULL,
  content     TEXT         NOT NULL,
  tags        JSON         NOT NULL,         -- 例如 ["top","normal"]
  notice_time DATETIME     NOT NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 3) activities 活动表 ============
CREATE TABLE activities (
  id           INT          NOT NULL,
  title        VARCHAR(200) NOT NULL,
  organizer    VARCHAR(100) NOT NULL,
  leader_id    VARCHAR(20)  NOT NULL,
  leader_name  VARCHAR(50)  NOT NULL,
  activity_date DATE        NULL,            -- date: 2026-01-22
  activity_time VARCHAR(10) NULL,            -- time: "14:00"
  start_at     DATETIME     NOT NULL,
  end_at       DATETIME     NOT NULL,
  location     VARCHAR(200) NOT NULL,
  summary      VARCHAR(255) NULL,
  content      TEXT         NULL,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_activities_leader FOREIGN KEY (leader_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 4) activity_create_applications 活动创建申请/审批表 ============
CREATE TABLE activity_create_applications (
  apply_id     VARCHAR(30)  NOT NULL,
  title        VARCHAR(200) NOT NULL,
  organizer    VARCHAR(100) NOT NULL,
  leader_id    VARCHAR(20)  NOT NULL,
  leader_name  VARCHAR(50)  NOT NULL,
  start_at     DATETIME     NOT NULL,
  end_at       DATETIME     NOT NULL,
  location     VARCHAR(200) NOT NULL,
  summary      VARCHAR(255) NULL,
  content      TEXT         NULL,
  submitted_at DATETIME     NOT NULL,
  status       VARCHAR(20)  NOT NULL,        -- 待审核/已同意/已拒绝
  auditor_id   VARCHAR(20)  NULL,
  auditor_name VARCHAR(50)  NULL,
  audited_at   DATETIME     NULL,
  comment      VARCHAR(255) NULL,
  PRIMARY KEY (apply_id),
  CONSTRAINT fk_aca_leader  FOREIGN KEY (leader_id)  REFERENCES users(id),
  CONSTRAINT fk_aca_auditor FOREIGN KEY (auditor_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 5) profile_change_applications 用户资料变更申请/审核表 ============
CREATE TABLE profile_change_applications (
  apply_id       VARCHAR(30)  NOT NULL,
  target_user_id VARCHAR(20)  NOT NULL,      -- 被变更用户
  target_user_name VARCHAR(50) NOT NULL,
  applicant_id   VARCHAR(20)  NOT NULL,      -- 发起人
  applicant_name VARCHAR(50)  NOT NULL,
  change_type    VARCHAR(50)  NOT NULL,      -- 电话变更/所属部门变更/性别变更
  before_value   VARCHAR(255) NULL,
  after_value    VARCHAR(255) NULL,
  submitted_at   DATETIME     NOT NULL,
  status         VARCHAR(20)  NOT NULL,      -- 待审核/已同意/已拒绝
  approval_comment VARCHAR(255) NULL,
  PRIMARY KEY (apply_id),
  CONSTRAINT fk_pca_target    FOREIGN KEY (target_user_id) REFERENCES users(id),
  CONSTRAINT fk_pca_applicant FOREIGN KEY (applicant_id)   REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 6) activity_leave_applications 活动请假申请/审批表 ============
CREATE TABLE activity_leave_applications (
  apply_id       VARCHAR(30)  NOT NULL,
  activity_id    INT          NOT NULL,
  activity_title VARCHAR(200) NOT NULL,
  teacher_id     VARCHAR(20)  NOT NULL,
  teacher_name   VARCHAR(50)  NOT NULL,
  applicant_id   VARCHAR(20)  NOT NULL,
  applicant_name VARCHAR(50)  NOT NULL,
  reason         VARCHAR(500) NOT NULL,
  submitted_at   DATETIME     NOT NULL,
  status         VARCHAR(20)  NOT NULL,      -- 待审核/已同意/已拒绝
  approval_comment VARCHAR(255) NULL,
  PRIMARY KEY (apply_id),
  CONSTRAINT fk_ala_activity  FOREIGN KEY (activity_id)  REFERENCES activities(id),
  CONSTRAINT fk_ala_teacher   FOREIGN KEY (teacher_id)   REFERENCES users(id),
  CONSTRAINT fk_ala_applicant FOREIGN KEY (applicant_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 7) user_activity_rel 用户-活动关系表（参加/管理） ============
CREATE TABLE user_activity_rel (
  user_id       VARCHAR(20) NOT NULL,
  activity_id   INT         NOT NULL,
  relation_type VARCHAR(20) NOT NULL,  -- join / manage
  created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, activity_id, relation_type),
  CONSTRAINT fk_uar_user    FOREIGN KEY (user_id)    REFERENCES users(id),
  CONSTRAINT fk_uar_activity FOREIGN KEY (activity_id) REFERENCES activities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 8) activity_attendance 活动出勤/签到表 ============
CREATE TABLE activity_attendance (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  activity_id  INT         NOT NULL,
  user_id      VARCHAR(20) NOT NULL,
  user_name    VARCHAR(50) NOT NULL,
  sign_in_at   DATETIME    NULL,
  attend_status VARCHAR(20) NOT NULL, -- 未出勤/已出勤/请假
  leave_apply_id VARCHAR(30) NULL,
  is_late      TINYINT(1)  NOT NULL DEFAULT 0,
  created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_attendance (activity_id, user_id),
  CONSTRAINT fk_att_activity FOREIGN KEY (activity_id) REFERENCES activities(id),
  CONSTRAINT fk_att_user     FOREIGN KEY (user_id)     REFERENCES users(id),
  CONSTRAINT fk_att_leave    FOREIGN KEY (leave_apply_id) REFERENCES activity_leave_applications(apply_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 9) community_posts 社区帖子表（images JSON + views_count） ============
CREATE TABLE community_posts (
  id                VARCHAR(50)  NOT NULL,
  created_by_user_id VARCHAR(20)  NOT NULL,
  created_at         DATETIME     NOT NULL,
  type               VARCHAR(50)  NOT NULL,         -- activity_recommend / work_study ...
  related_activity_id INT         NULL,
  content            TEXT         NOT NULL,
  images             JSON         NOT NULL,         -- 图片路径数组
  views_count        INT          NOT NULL DEFAULT 0,
  created_at_db       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_cp_user     FOREIGN KEY (created_by_user_id) REFERENCES users(id),
  CONSTRAINT fk_cp_activity FOREIGN KEY (related_activity_id) REFERENCES activities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 10) community_comments 社区评论/回复表（parent_comment_id 支持回复） ============
CREATE TABLE community_comments (
  id                VARCHAR(50)  NOT NULL,
  post_id            VARCHAR(50)  NOT NULL,
  user_id            VARCHAR(20)  NOT NULL,
  user_name          VARCHAR(50)  NOT NULL,
  created_at         DATETIME     NOT NULL,
  content            VARCHAR(1000) NOT NULL,
  parent_comment_id  VARCHAR(50)  NULL,
  PRIMARY KEY (id),
  KEY idx_comments_post (post_id),
  KEY idx_comments_parent (parent_comment_id),
  CONSTRAINT fk_cc_post   FOREIGN KEY (post_id) REFERENCES community_posts(id),
  CONSTRAINT fk_cc_user   FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_cc_parent FOREIGN KEY (parent_comment_id) REFERENCES community_comments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 11) community_post_reactions 社区互动表（点赞/收藏合并） ============
CREATE TABLE community_post_reactions (
  id            BIGINT      NOT NULL AUTO_INCREMENT,
  post_id        VARCHAR(50) NOT NULL,
  user_id        VARCHAR(20) NOT NULL,
  reaction_type  ENUM('like','favorite') NOT NULL,
  created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_reaction (post_id, user_id, reaction_type),
  CONSTRAINT fk_cpr_post FOREIGN KEY (post_id) REFERENCES community_posts(id),
  CONSTRAINT fk_cpr_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- ====================== 插入 seed 数据 =====================
-- =========================================================

-- users（保留原始用户，并补充到 20 个）
INSERT INTO users (id, name, role, password, gender, avatar, department, college, phone)
VALUES
('0001','系统管理员','管理员','admin123','男','/profilePicture.jpg','信息中心','信息中心','13800000001'),
('2023001','张老师','教师','teacher123','女','/AnonymousAvatar.png','计算机学院',NULL,'13800000002'),
('2023002','李老师','教师','teacher123','男','/AnonymousAvatar.png','数学学院',NULL,'13800000003'),
('2024001','王辅导员','辅导员','counselor123','女','/AnonymousAvatar.png','学生工作处',NULL,'13800000004'),
('2024002','赵辅导员','辅导员','counselor123','男','/AnonymousAvatar.png','学生工作处',NULL,'13800000005'),
('2024123456','调试用户','学生','student123','男','/AnonymousAvatar.png','计算机学院',NULL,'13800000006'),
('2024010123','刘同学','学生','student123','女','/AnonymousAvatar.png','外国语学院',NULL,'13800000007'),
('2024020789','陈同学','学生','student123','男','/AnonymousAvatar.png','经济学院',NULL,'13800000008'),
('2023003','周老师','教师','teacher123','男','/AnonymousAvatar.png','自动化学院',NULL,'13800000009'),
('2023004','孙老师','教师','teacher123','女','/AnonymousAvatar.png','外国语学院',NULL,'13800000010'),
('2023005','何老师','教师','teacher123','男','/AnonymousAvatar.png','体育教学部',NULL,'13800000011'),
('2024003','林辅导员','辅导员','counselor123','女','/AnonymousAvatar.png','学生工作处',NULL,'13800000012'),
('2024030156','黄同学','学生','student123','女','/AnonymousAvatar.png','计算机学院',NULL,'13800000013'),
('2024030188','许同学','学生','student123','男','/AnonymousAvatar.png','管理学院',NULL,'13800000014'),
('2024030211','吴同学','学生','student123','女','/AnonymousAvatar.png','自动化学院',NULL,'13800000015'),
('2024030245','郑同学','学生','student123','男','/AnonymousAvatar.png','数学学院',NULL,'13800000016'),
('2024030302','杨同学','学生','student123','女','/AnonymousAvatar.png','机械工程学院',NULL,'13800000017'),
('2024030333','胡同学','学生','student123','男','/AnonymousAvatar.png','外国语学院',NULL,'13800000018'),
('2024030366','朱同学','学生','student123','女','/AnonymousAvatar.png','法学院',NULL,'13800000019'),
('2024030399','马同学','学生','student123','男','/AnonymousAvatar.png','材料与环境工程学院',NULL,'13800000020');

-- notices（丰富通知展示）
INSERT INTO notices (id, title, content, tags, notice_time)
VALUES
(1,'关于期末考试安排的通知','各学院、各位同学：

2025-2026 学年期末考试安排已经确定，请同学们提前做好复习准备。
具体考试时间、地点请登录教务系统进行查询。

请务必携带学生证参加考试，遵守考场纪律。

特此通知。',JSON_ARRAY('top','normal'),'2026-01-20 00:00:00'),
(2,'本周系统维护公告','为提升系统稳定性，学校信息系统将于本周进行例行维护。
维护期间部分功能可能暂时无法使用，请大家合理安排时间。

给您带来不便，敬请谅解。',JSON_ARRAY('normal'),'2026-01-01 00:00:00'),
(3,'春季学生活动月安排通知','为进一步丰富校园文化生活，学校将于 2 月下旬至 3 月上旬集中开展学术讲座、体育竞赛、志愿服务与心理健康主题活动。

请各学院积极组织同学参与，具体安排以系统内活动列表为准。',JSON_ARRAY('top'),'2026-02-15 09:00:00'),
(4,'图书馆开放时间调整提醒','因新学期读者服务安排调整，自 2026 年 2 月 17 日起，图书馆工作日开放时间调整为 08:00-22:00。
周末及节假日开放时间请关注后续通知。',JSON_ARRAY('normal'),'2026-02-16 08:30:00');

-- activities（保留原始活动，并补充到 10 个）
INSERT INTO activities
(id, title, organizer, leader_id, leader_name, activity_date, activity_time, start_at, end_at, location, summary, content)
VALUES
(1,'人工智能前沿学术讲座','计算机学院','2023001','张老师','2026-01-22','14:00','2026-01-22 14:00:00','2026-01-22 16:00:00','学活剧院','围绕大模型与智能系统最新研究展开，含现场答疑。','本次讲座将围绕人工智能在大模型、智能系统方向的最新研究成果展开。

主讲人将分享科研经验，并就同学们关心的问题进行现场答疑。
欢迎相关专业同学积极参与。'),
(2,'校级程序设计竞赛','教务处','2023001','张老师','2026-01-25','09:00','2026-01-25 09:00:00','2026-01-25 12:00:00','信息楼301','面向全校本科生，现场编程竞赛，优秀选手有校级奖励。','本次竞赛面向全校本科生，采取现场编程形式。

参赛同学需提前报名，比赛当天携带学生证。
优秀选手将获得校级奖励。'),
(3,'图书馆阅读推广志愿服务','校团委','2024001','王辅导员','2026-02-18','14:30','2026-02-18 14:30:00','2026-02-18 17:00:00','图书馆北门服务台','招募学生参与阅读推广、秩序维护和读者引导。','本次志愿服务面向全校学生开放，主要内容包括借阅引导、活动签到协助、阅读角维护等。

参与同学需提前 10 分钟集合，统一领取志愿服务马甲后上岗。'),
(4,'英语角交流活动','外国语学院','2023004','孙老师','2026-02-19','19:00','2026-02-19 19:00:00','2026-02-19 20:30:00','图书馆一楼报告厅','主题交流 + 分组讨论，提升口语表达与跨文化沟通能力。','活动包含自由交流、主题讨论和现场点评三个环节。

欢迎想提升英语表达能力、了解国际交流话题的同学报名参与。'),
(5,'数学建模竞赛经验分享会','数学学院','2023002','李老师','2026-02-21','14:00','2026-02-21 14:00:00','2026-02-21 16:30:00','数学楼201','邀请往届获奖同学分享选题、建模与论文写作经验。','本次分享会将系统介绍数学建模竞赛的选题思路、团队分工、论文撰写与答辩准备方法。

适合有意参加建模竞赛的同学提前了解比赛流程。'),
(6,'春季实习简历工作坊','学生就业指导中心','2024002','赵辅导员','2026-02-23','18:30','2026-02-23 18:30:00','2026-02-23 20:30:00','学生活动中心201','围绕简历结构优化、项目经历表达和面试准备开展指导。','本次工作坊将从简历排版、项目经历写法、自我介绍设计等方面进行实操讲解。

现场设置修改建议环节，欢迎准备春季实习的同学参加。'),
(7,'羽毛球师生友谊赛','体育教学部','2023005','何老师','2026-02-26','15:00','2026-02-26 15:00:00','2026-02-26 17:30:00','体育馆羽毛球馆','师生混合组队参赛，倡导健康运动与交流合作。','活动采用分组循环赛形式进行，设置学生单打、双打与师生配对赛。

欢迎喜爱羽毛球运动的老师和同学积极报名参加。'),
(8,'创新创业项目路演训练营','创新创业学院','2023003','周老师','2026-03-02','13:30','2026-03-02 13:30:00','2026-03-02 16:30:00','创新创业中心路演厅','聚焦项目表达、路演节奏和商业计划书展示技巧。','训练营将结合典型案例讲解路演结构设计、PPT 精简表达与现场问答技巧。

适合准备参加创新创业赛事和项目评审的同学参与。'),
(9,'心理健康团体辅导工作坊','心理健康教育中心','2024003','林辅导员','2026-03-05','19:00','2026-03-05 19:00:00','2026-03-05 20:30:00','心理中心团辅室','通过互动体验帮助同学缓解学业压力、提升情绪调节能力。','工作坊围绕压力识别、情绪表达和同伴支持展开，采用小组互动、案例讨论和放松练习相结合的方式进行。

名额有限，请按要求准时签到。'),
(10,'校园环保实践活动','校学生会','2024001','王辅导员','2026-03-08','08:30','2026-03-08 08:30:00','2026-03-08 11:30:00','东区操场集合','开展校园清洁、垃圾分类宣传和绿色校园倡议实践。','本次环保实践活动将分小组开展校园清洁、垃圾分类宣传和绿色倡议打卡。

完成活动的同学可获得志愿服务记录。');

-- activity_create_applications（活动创建审批：待审核 / 已同意 / 已拒绝）
INSERT INTO activity_create_applications
(apply_id, title, organizer, leader_id, leader_name, start_at, end_at, location, summary, content, submitted_at, status, auditor_id, auditor_name, audited_at, comment)
VALUES
('AC-20260206-0001','数据结构算法训练营','计算机学院','2023002','李老师','2026-02-20 18:30:00','2026-02-20 20:30:00','信息楼201','面向大一大二，算法基础训练。','包含基础题讲解、现场练习和答疑。','2026-02-06 10:20:00','待审核',NULL,NULL,NULL,NULL),
('AC-20260201-0002','英语角交流活动','外国语学院','2023001','张老师','2026-02-12 19:00:00','2026-02-12 20:30:00','图书馆一楼报告厅','自由交流+主题讨论，提升口语与表达。','活动包含分组交流、主题讨论和老师点评，欢迎报名参与。','2026-02-01 09:20:00','已同意','0001','系统管理员','2026-02-01 10:05:00','材料齐全，批准创建。'),
('AC-20260208-0003','春季实习简历工作坊','学生就业指导中心','2024002','赵辅导员','2026-02-23 18:30:00','2026-02-23 20:30:00','学生活动中心201','围绕春招实习准备开展简历优化指导。','内容包含简历修改、项目经历提炼和模拟提问环节。','2026-02-08 14:10:00','已同意','0001','系统管理员','2026-02-08 15:00:00','主题明确，流程完整，同意创建。'),
('AC-20260210-0004','创新创业项目路演训练营','创新创业学院','2023003','周老师','2026-03-02 13:30:00','2026-03-02 16:30:00','创新创业中心路演厅','面向科创团队开展项目路演专项训练。','包含展示节奏训练、商业计划书优化和评委模拟问答。','2026-02-10 16:30:00','已同意','0001','系统管理员','2026-02-10 17:10:00','场地与流程准备充分，准予开展。'),
('AC-20260211-0005','夜跑打卡活动','管理学院学生会','2024030188','许同学','2026-02-28 20:00:00','2026-02-28 21:00:00','东操场','以夜跑打卡形式促进同学锻炼。','计划采用签到打卡与里程记录相结合的方式开展。','2026-02-11 19:20:00','已拒绝','0001','系统管理员','2026-02-12 09:10:00','安全预案与应急联系人信息不完整，暂不通过。');

-- profile_change_applications（资料变更审批：待审核 / 已同意 / 已拒绝）
INSERT INTO profile_change_applications
(apply_id, target_user_id, target_user_name, applicant_id, applicant_name, change_type, before_value, after_value, submitted_at, status, approval_comment)
VALUES
('AR-20260201-0001','2024123456','调试用户','2024123456','调试用户','电话变更','13800000006','13900000006','2026-02-01 10:12:00','待审核',NULL),
('AR-20260202-0002','2024010123','刘同学','0001','系统管理员','所属部门变更','外国语学院','计算机学院','2026-02-02 14:30:00','已同意','材料齐全，同意变更。'),
('AR-20260203-0003','2024020789','陈同学','2024020789','陈同学','性别变更','男','女','2026-02-03 09:05:00','已拒绝',NULL),
('AR-20260204-0004','2024030156','黄同学','2024030156','黄同学','电话变更','13800000013','13910000013','2026-02-04 12:40:00','已同意','信息核验通过，同意修改。'),
('AR-20260205-0005','2024003','林辅导员','0001','系统管理员','所属部门变更','学生工作处','材料与环境工程学院','2026-02-05 15:20:00','待审核',NULL),
('AR-20260206-0006','2024030366','朱同学','2024030366','朱同学','性别变更','女','男','2026-02-06 08:50:00','已拒绝','请先补充教务备案材料后再提交申请。');

-- activity_leave_applications（活动请假审批：待审核 / 已同意 / 已拒绝）
INSERT INTO activity_leave_applications
(apply_id, activity_id, activity_title, teacher_id, teacher_name, applicant_id, applicant_name, reason, submitted_at, status, approval_comment)
VALUES
('LA-20260201-0001',2,'校级程序设计竞赛','2023001','张老师','2024123456','调试用户','与学院安排的实验课时间冲突，申请请假。','2026-02-01 11:20:00','待审核',NULL),
('LA-20260202-0002',2,'校级程序设计竞赛','2023001','张老师','2024010123','刘同学','当天需要参加外语学院组织的面试，时间冲突。','2026-02-02 09:35:00','已同意','情况属实，已批准请假。'),
('LA-20260203-0003',2,'校级程序设计竞赛','2023001','张老师','2024020789','陈同学','个人原因无法到场。','2026-02-03 16:10:00','已拒绝','理由不充分，请按要求参加活动。'),
('LA-20260204-0004',4,'英语角交流活动','2023004','孙老师','2024030333','胡同学','与学院志愿培训时间冲突，申请请假。','2026-02-18 20:10:00','已同意','情况属实，已登记请假。'),
('LA-20260205-0005',7,'羽毛球师生友谊赛','2023005','何老师','2024030302','杨同学','当天身体不适，担心无法正常参加比赛。','2026-02-25 18:30:00','待审核',NULL),
('LA-20260206-0006',8,'创新创业项目路演训练营','2023003','周老师','2024030188','许同学','与专业实验课程时间冲突，申请请假。','2026-03-01 09:40:00','已拒绝','经核实课程可调课，请按时参加训练营。'),
('LA-20260207-0007',9,'心理健康团体辅导工作坊','2024003','林辅导员','2024030399','马同学','需前往医院复查，无法按时参加活动。','2026-03-04 14:25:00','已同意','已查看证明材料，同意请假。');

-- user_activity_rel（覆盖 join / manage 两类关系）
INSERT INTO user_activity_rel (user_id, activity_id, relation_type)
VALUES
('0001',1,'join'),
('0001',2,'join'),
('0001',1,'manage'),
('0001',2,'manage'),
('2023001',2,'manage'),
('2024123456',2,'join'),
('2024010123',2,'join'),
('2024020789',2,'join'),
('2024010123',1,'join'),
('2024030156',1,'join'),
('2024030211',1,'join'),
('2024001',3,'manage'),
('2024030156',3,'join'),
('2024030366',3,'join'),
('2024030399',3,'join'),
('2024010123',3,'join'),
('2023004',4,'manage'),
('2024010123',4,'join'),
('2024030333',4,'join'),
('2024030211',4,'join'),
('2024030366',4,'join'),
('2023002',5,'manage'),
('2024020789',5,'join'),
('2024030245',5,'join'),
('2024030188',5,'join'),
('2024030302',5,'join'),
('2024002',6,'manage'),
('2024123456',6,'join'),
('2024030188',6,'join'),
('2024030302',6,'join'),
('2024030399',6,'join'),
('2023005',7,'manage'),
('2024030211',7,'join'),
('2024030245',7,'join'),
('2024030302',7,'join'),
('2024030156',7,'join'),
('2023003',8,'manage'),
('2024030188',8,'join'),
('2024030399',8,'join'),
('2024123456',8,'join'),
('2024020789',8,'join'),
('2024003',9,'manage'),
('2024030366',9,'join'),
('2024030399',9,'join'),
('2024030333',9,'join'),
('2024001',10,'manage'),
('2024010123',10,'join'),
('2024030156',10,'join'),
('2024030211',10,'join'),
('2024030302',10,'join'),
('2024030366',10,'join');

-- activity_attendance（覆盖 已出勤 / 请假 / 未出勤 / 迟到）
INSERT INTO activity_attendance
(activity_id, user_id, user_name, sign_in_at, attend_status, leave_apply_id, is_late)
VALUES
(1,'2024010123','刘同学',NULL,'未出勤',NULL,0),
(1,'0001','系统管理员',NULL,'未出勤',NULL,0),
(1,'2024030156','黄同学','2026-01-22 13:54:00','已出勤',NULL,0),
(1,'2024030211','吴同学','2026-01-22 14:08:00','已出勤',NULL,1),
(2,'2024123456','调试用户','2026-01-25 08:55:00','已出勤',NULL,1),
(2,'2024010123','刘同学',NULL,'请假','LA-20260202-0002',0),
(2,'2024020789','陈同学',NULL,'未出勤',NULL,0),
(2,'2024030245','郑同学','2026-01-25 08:52:00','已出勤',NULL,0),
(2,'2024030188','许同学','2026-01-25 09:03:00','已出勤',NULL,1),
(3,'2024030156','黄同学','2026-02-18 14:20:00','已出勤',NULL,0),
(3,'2024030366','朱同学','2026-02-18 14:26:00','已出勤',NULL,0),
(3,'2024030399','马同学',NULL,'未出勤',NULL,0),
(3,'2024010123','刘同学','2026-02-18 14:35:00','已出勤',NULL,1),
(4,'2024010123','刘同学','2026-02-19 18:50:00','已出勤',NULL,0),
(4,'2024030333','胡同学',NULL,'请假','LA-20260204-0004',0),
(4,'2024030211','吴同学','2026-02-19 19:05:00','已出勤',NULL,1),
(4,'2024030366','朱同学','2026-02-19 18:57:00','已出勤',NULL,0),
(5,'2024020789','陈同学','2026-02-21 13:50:00','已出勤',NULL,0),
(5,'2024030245','郑同学','2026-02-21 13:58:00','已出勤',NULL,0),
(5,'2024030188','许同学','2026-02-21 14:12:00','已出勤',NULL,1),
(5,'2024030302','杨同学',NULL,'未出勤',NULL,0),
(6,'2024123456','调试用户','2026-02-23 18:25:00','已出勤',NULL,0),
(6,'2024030188','许同学','2026-02-23 18:27:00','已出勤',NULL,0),
(6,'2024030302','杨同学','2026-02-23 18:41:00','已出勤',NULL,1),
(6,'2024030399','马同学','2026-02-23 18:32:00','已出勤',NULL,0),
(7,'2024030211','吴同学','2026-02-26 14:55:00','已出勤',NULL,0),
(7,'2024030245','郑同学','2026-02-26 15:04:00','已出勤',NULL,1),
(7,'2024030302','杨同学',NULL,'未出勤',NULL,0),
(7,'2024030156','黄同学','2026-02-26 14:57:00','已出勤',NULL,0),
(8,'2024030188','许同学',NULL,'未出勤',NULL,0),
(8,'2024030399','马同学','2026-03-02 13:25:00','已出勤',NULL,0),
(8,'2024123456','调试用户','2026-03-02 13:36:00','已出勤',NULL,1),
(8,'2024020789','陈同学','2026-03-02 13:31:00','已出勤',NULL,0),
(9,'2024030366','朱同学','2026-03-05 18:53:00','已出勤',NULL,0),
(9,'2024030399','马同学',NULL,'请假','LA-20260207-0007',0),
(9,'2024030333','胡同学','2026-03-05 19:07:00','已出勤',NULL,1),
(10,'2024010123','刘同学','2026-03-08 08:20:00','已出勤',NULL,0),
(10,'2024030156','黄同学','2026-03-08 08:29:00','已出勤',NULL,0),
(10,'2024030211','吴同学','2026-03-08 08:35:00','已出勤',NULL,1),
(10,'2024030302','杨同学','2026-03-08 08:31:00','已出勤',NULL,0),
(10,'2024030366','朱同学',NULL,'未出勤',NULL,0);

-- community_posts（按你的要求：社区内容保持不动）
INSERT INTO community_posts
(id, created_by_user_id, created_at, type, related_activity_id, content, images, views_count)
VALUES
('post-teacher-0001','2023001','2026-02-06 09:15:00','activity_recommend',1,'【活动推荐】人工智能前沿学术讲座来啦！本次会围绕大模型与智能系统的最新研究展开，也会有答疑环节。欢迎同学们来听～',JSON_ARRAY('/community/teacher1/1.jpg'),520),
('post-counselor-0001','2024001','2026-02-05 16:20:00','work_study',NULL,'【勤工俭学招聘】图书馆与信息中心现开放若干勤工助学岗位：
1）图书整理/咨询引导
2）机房值班/设备登记
要求：认真负责、每周可固定排班。
报名方式：请到“个人中心-申请”提交材料，截止 2 月 10 日。',JSON_ARRAY(),310),
('post-student-0001','2024123456','2026-02-04 21:10:00','activity_recommend',2,'【活动推荐】校级程序设计竞赛真的很值得参加！我把报名流程和准备资料整理成九宫格了，想参赛的同学可以参考～一起冲！',JSON_ARRAY('/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg','/community/student1/1.jpg'),860);

-- community_comments（保持原样）
INSERT INTO community_comments
(id, post_id, user_id, user_name, created_at, content, parent_comment_id)
VALUES
('c-t-0001','post-teacher-0001','2024010123','刘同学','2026-02-06 09:40:00','老师需要提前报名吗？',NULL),
('r-t-0001','post-teacher-0001','2023001','张老师','2026-02-06 09:48:00','不需要报名，提前到场签到即可～','c-t-0001'),
('c-w-0001','post-counselor-0001','2024123456','调试用户','2026-02-05 16:45:00','请问每周大概需要排班几次？',NULL),
('r-w-0001','post-counselor-0001','2024001','王辅导员','2026-02-05 16:55:00','一般每周 2-3 次，每次 2 小时左右，会根据课表协调。','c-w-0001'),
('c-s-0001','post-student-0001','2023002','李老师','2026-02-04 21:40:00','整理得很细致，祝你比赛取得好成绩！',NULL),
('r-s-0001','post-student-0001','2024123456','调试用户','2026-02-04 21:45:00','谢谢老师！我会继续加油的！','c-s-0001');

-- community_post_reactions（保持原样）
INSERT INTO community_post_reactions (post_id, user_id, reaction_type)
VALUES
('post-teacher-0001','0001','favorite'),
('post-counselor-0001','0001','favorite'),
('post-student-0001','0001','favorite'),
('post-student-0001','0001','like');

DROP TABLE IF EXISTS ai_messages;
DROP TABLE IF EXISTS ai_conversations;
DROP TABLE IF EXISTS message_messages;
DROP TABLE IF EXISTS message_conversations;

-- ============ AI 会话表 ============
CREATE TABLE ai_conversations (
  id         VARCHAR(50)  NOT NULL,
  title      VARCHAR(200) NOT NULL,
  created_at DATETIME     NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ AI 消息表 ============
CREATE TABLE ai_messages (
  id              BIGINT      NOT NULL AUTO_INCREMENT,
  conversation_id VARCHAR(50) NOT NULL,
  role            ENUM('assistant','user') NOT NULL,
  content         TEXT        NOT NULL,
  created_at      DATETIME    NOT NULL,
  seq             INT         NOT NULL,
  PRIMARY KEY (id),
  KEY idx_ai_msg_conv (conversation_id, seq),
  CONSTRAINT fk_ai_msg_conv FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 消息中心会话表 ============
CREATE TABLE message_conversations (
  id         VARCHAR(50)  NOT NULL,
  name       VARCHAR(100) NOT NULL,
  avatar     VARCHAR(255) NULL,
  updated_at DATETIME     NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 消息中心消息表 ============
CREATE TABLE message_messages (
  id              BIGINT      NOT NULL AUTO_INCREMENT,
  conversation_id VARCHAR(50) NOT NULL,
  role            ENUM('system','other','self') NOT NULL,
  content         TEXT        NOT NULL,
  sent_at         DATETIME    NOT NULL,
  seq             INT         NOT NULL,
  PRIMARY KEY (id),
  KEY idx_msg_conv (conversation_id, seq),
  CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES message_conversations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================= 插入 AI 与消息中心 mock 数据 =================

-- AI conversations（去除调试味过重的记录，保留“提问活动信息”主题，并新增一组更贴合系统主题的问答）
INSERT INTO ai_conversations (id, title, created_at)
VALUES
('conv-20260301-activity','活动信息咨询','2026-03-01 09:00:00'),
('conv-20260306-attendance','请假与签到说明','2026-03-06 20:10:00');

INSERT INTO ai_messages (conversation_id, role, content, created_at, seq)
VALUES
('conv-20260301-activity','user','这周学校有哪些值得参加的活动？','2026-03-01 09:00:05',1),
('conv-20260301-activity','assistant','本周可以重点关注三场活动：3 月 2 日的“创新创业项目路演训练营”、3 月 5 日的“心理健康团体辅导工作坊”，以及 3 月 8 日的“校园环保实践活动”。如果你更关注竞赛能力或项目展示训练，我更推荐路演训练营。','2026-03-01 09:00:12',2),
('conv-20260301-activity','user','我比较想参加和竞赛、项目能力提升相关的，有推荐吗？','2026-03-01 09:00:20',3),
('conv-20260301-activity','assistant','优先推荐“创新创业项目路演训练营”，它更偏向项目展示、答辩表达和商业计划书梳理；如果你还想补充竞赛经验，也可以回看“数学建模竞赛经验分享会”的相关资料。另有“数据结构算法训练营”申请正在审核，通过后也很适合想提升算法能力的同学关注。','2026-03-01 09:00:32',4),
('conv-20260306-attendance','user','如果我报名了活动但临时有课，应该怎么请假并查看签到状态？','2026-03-06 20:10:05',1),
('conv-20260306-attendance','assistant','你可以先在请假申请中提交 activityId、applicantId 和请假原因，审核通过后，系统会在对应活动的考勤记录里体现请假结果。','2026-03-06 20:10:14',2),
('conv-20260306-attendance','user','请假通过后，考勤里会怎么显示？','2026-03-06 20:10:22',3),
('conv-20260306-attendance','assistant','通常会显示为“请假”，并关联对应的请假申请编号；如果活动当天完成签到，则会显示“已出勤”，迟到会额外标记 isLate=true。这样老师在活动考勤页面就能区分正常出勤、请假和未到场三种情况。','2026-03-06 20:10:35',4);

-- 消息中心 conversations（增加教师提醒、学生交流、活动联络群等）
INSERT INTO message_conversations (id, name, avatar, updated_at)
VALUES
('msg-1','教务处','/system.png','2026-01-26 09:35:00'),
('msg-2','张老师','/teacher.png','2026-01-25 18:20:00'),
('msg-3','王辅导员','/AnonymousAvatar.png','2026-02-18 19:50:00'),
('msg-4','刘同学','/AnonymousAvatar.png','2026-02-20 21:15:00'),
('msg-5','程序设计竞赛交流群','/system.png','2026-02-24 21:15:00'),
('msg-6','英语角活动联络群','/system.png','2026-02-19 18:45:00'),
('msg-7','李老师','/teacher.png','2026-03-02 11:25:00');

INSERT INTO message_messages (conversation_id, role, content, sent_at, seq)
VALUES
('msg-1','system','各位同学请注意。','2026-01-26 09:20:00',1),
('msg-1','system','2025-2026 学年期末考试安排已公布。','2026-01-26 09:25:00',2),
('msg-1','system','请登录教务系统查看具体时间。','2026-01-26 09:30:00',3),
('msg-1','system','如考试时间与已审批活动冲突，请及时联系学院老师说明情况。','2026-01-26 09:35:00',4),
('msg-2','other','这周的作业难度不大。','2026-01-25 18:00:00',1),
('msg-2','other','记得周五前提交。','2026-01-25 18:10:00',2),
('msg-2','self','老师，附件格式有什么要求？','2026-01-25 18:15:00',3),
('msg-2','other','PDF 或 Word 都可以，命名写学号和姓名即可。','2026-01-25 18:20:00',4),
('msg-3','other','明天下午的图书馆志愿服务请提前 10 分钟到场。','2026-02-18 19:40:00',1),
('msg-3','other','请大家带好学生证，现场统一领取志愿者马甲。','2026-02-18 19:43:00',2),
('msg-3','self','收到，我会提前到。','2026-02-18 19:46:00',3),
('msg-3','other','如需请假请在今晚 20:00 前完成申请提交。','2026-02-18 19:50:00',4),
('msg-4','other','你周六去数学建模竞赛经验分享会吗？','2026-02-20 21:05:00',1),
('msg-4','self','去的，我想重点听一下选题和论文写作部分。','2026-02-20 21:09:00',2),
('msg-4','other','那我们到时候一起过去，我提前帮你占个前排位置。','2026-02-20 21:15:00',3),
('msg-5','other','大家都准备报名校级程序设计竞赛吗？','2026-02-24 20:55:00',1),
('msg-5','self','我已经报了，周末准备刷两场练习赛。','2026-02-24 21:02:00',2),
('msg-5','other','我整理了往年题单，晚点发群里。','2026-02-24 21:08:00',3),
('msg-5','other','好的，我也来一起练，争取拿个好成绩。','2026-02-24 21:15:00',4),
('msg-6','other','今晚英语角主题是“校园生活与国际交流”。','2026-02-19 18:30:00',1),
('msg-6','other','现场会有分组讨论和自由交流两个环节。','2026-02-19 18:36:00',2),
('msg-6','self','需要提前签到吗？','2026-02-19 18:40:00',3),
('msg-6','other','需要，18:40 开始签到，地点在图书馆一楼报告厅门口。','2026-02-19 18:45:00',4),
('msg-7','other','创新创业路演训练营的展示材料记得控制在 5 页内。','2026-03-02 11:05:00',1),
('msg-7','self','老师，能否加入项目分工那一页？','2026-03-02 11:12:00',2),
('msg-7','other','可以，重点突出你的核心贡献和目标用户。','2026-03-02 11:18:00',3),
('msg-7','other','今晚 18:30 会开放彩排场地，需要的话可以提前来试讲。','2026-03-02 11:25:00',4);
