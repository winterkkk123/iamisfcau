// src/mock/appState.ts
export const appStateMock = {
  /* ============ 用户信息 user ============ */
  user: {
    isLogin: true,
    name: '系统管理员',
    id: '0001',
    role: '管理员',
    college: '信息中心',
    avatar: '/profilePicture.jpg',
    joinedActivityIds: [1, 2],
    managedActivityIds: [1, 2],
    communityMyFavorites: ['post-teacher-0001', 'post-counselor-0001'],
    communityMyPosts: ['post-student-0001']
  },

  /* ============ 通知 notices ============ */
  notices: [
    {
      id: 1,
      tags: ['top', 'normal'],
      title: '关于期末考试安排的通知',
      time: '2026-01-20T00:00:00+08:00',
      content: `
各学院、各位同学：

2025-2026 学年期末考试安排已经确定，请同学们提前做好复习准备。
具体考试时间、地点请登录教务系统进行查询。

请务必携带学生证参加考试，遵守考场纪律。

特此通知。
`
    },
    {
      id: 2,
      tags: ['normal'],
      title: '本周系统维护公告',
      time: '2026-01-01T00:00:00+08:00',
      content: `
为提升系统稳定性，学校信息系统将于本周进行例行维护。
维护期间部分功能可能暂时无法使用，请大家合理安排时间。

给您带来不便，敬请谅解。
`
    }
  ],

  /* ============ 活动列表 activities ============ */
  activities: [
    {
      id: 1,
      title: '人工智能前沿学术讲座',
      organizer: '计算机学院',
      leaderId: '2023001',
      leaderName: '张老师',
      date: '2026-01-22T00:00:00+08:00',
      time: '14:00',
      startAt: '2026-01-22T14:00:00+08:00',
      location: '学活剧院',
      content: `
本次讲座将围绕人工智能在大模型、智能系统方向的最新研究成果展开。

主讲人将分享科研经验，并就同学们关心的问题进行现场答疑。
欢迎相关专业同学积极参与。
`,
      summary: '围绕大模型与智能系统最新研究展开，含现场答疑。',
      endAt: '2026-01-22T16:00:00+08:00',
      attendance: [
        {
          userId: '2024010123',
          userName: '刘同学',
          signInAt: '',
          attendStatus: '未出勤',
          isLate: false
        },
        { 
          userId: '0001', 
          userName: '系统管理员', 
          signInAt: '', 
          attendStatus: '未出勤',
          isLate: false 
        }
      ]
    },
    {
      id: 2,
      title: '校级程序设计竞赛',
      organizer: '教务处',
      leaderId: '2023001',
      leaderName: '张老师',
      date: '2026-01-25T00:00:00+08:00',
      time: '09:00',
      startAt: '2026-01-25T09:00:00+08:00',
      location: '信息楼301',
      content: `
本次竞赛面向全校本科生，采取现场编程形式。

参赛同学需提前报名，比赛当天携带学生证。
优秀选手将获得校级奖励。
`,
      summary: '面向全校本科生，现场编程竞赛，优秀选手有校级奖励。',
      endAt: '2026-01-25T12:00:00+08:00',
      attendance: [
        {
          userId: '2024123456',
          userName: '调试用户',
          signInAt: '2026-01-25T08:55:00+08:00',
          attendStatus: '已出勤',
          isLate: true
        },
        {
          userId: '2024010123',
          userName: '刘同学',
          signInAt: '',
          attendStatus: '请假',
          leaveApplyId: 'LA-20260202-0002',
          isLate: false
        },
        {
          userId: '2024020789',
          userName: '陈同学',
          signInAt: '',
          attendStatus: '未出勤',
          isLate: false
        }
      ]
    }
  ],

  /* ============ 智能问答会话列表 conversations ============ */
  conversations: [
    {
      id: 'conv-20260101',
      title: '智能助手使用说明',
      createdAt: '2026-01-01T10:00:00+08:00',
      messages: [
        { role: 'assistant', content: '你好！我是高校智能助手，可以为你提供学习与系统使用方面的帮助。' },
        { role: 'user', content: '你能帮我做什么？' },
        { role: 'assistant', content: '我可以解答课程问题、提供系统指引，并辅助你的学习规划。' }
      ]
    }
  ],

  /* ============ 当前激活的问答会话 activeConversationId ============ */
  activeConversationId: 'conv-20260101',

  /* ============ 消息会话列表 messageConversations ============ */
  messageConversations: [
    {
      id: 'msg-1',
      name: '教务处',
      avatar: '/system.png',
      updatedAt: '2026-01-26T09:30:00+08:00',
      messages: [
        { role: 'system', content: '各位同学请注意。', time: '2026-01-26T09:20:00+08:00' },
        { role: 'system', content: '2025-2026 学年期末考试安排已公布。', time: '2026-01-26T09:25:00+08:00' },
        { role: 'system', content: '请登录教务系统查看具体时间。', time: '2026-01-26T09:30:00+08:00' }
      ]
    },
    {
      id: 'msg-2',
      name: '张老师',
      avatar: '/teacher.png',
      updatedAt: '2026-01-25T18:10:00+08:00',
      messages: [
        { role: 'other', content: '这周的作业难度不大。', time: '2026-01-25T18:00:00+08:00' },
        { role: 'other', content: '记得周五前提交。', time: '2026-01-25T18:10:00+08:00' }
      ]
    }
  ],

  /* ============ 用户管理 users ============ */
  users: [
    {
      id: '0001',
      name: '系统管理员',
      role: '管理员',
      password: 'admin123',
      gender: '男',
      avatar: '/profilePicture.jpg',
      department: '信息中心',
      phone: '13800000001',
      joinedActivityIds: [1, 2],
      managedActivityIds: [1, 2]
    },
    {
      id: '2023001',
      name: '张老师',
      role: '教师',
      password: 'teacher123',
      gender: '女',
      avatar: '/AnonymousAvatar.png',
      department: '计算机学院',
      phone: '13800000002',
      joinedActivityIds: [],
      managedActivityIds: [2]
    },
    {
      id: '2023002',
      name: '李老师',
      role: '教师',
      password: 'teacher123',
      gender: '男',
      avatar: '/AnonymousAvatar.png',
      department: '数学学院',
      phone: '13800000003',
      joinedActivityIds: [],
      managedActivityIds: []
    },
    {
      id: '2024001',
      name: '王辅导员',
      role: '辅导员',
      password: 'counselor123',
      gender: '女',
      avatar: '/AnonymousAvatar.png',
      department: '学生工作处',
      phone: '13800000004',
      joinedActivityIds: [],
      managedActivityIds: []
    },
    {
      id: '2024002',
      name: '赵辅导员',
      role: '辅导员',
      password: 'counselor123',
      gender: '男',
      avatar: '/AnonymousAvatar.png',
      department: '学生工作处',
      phone: '13800000005',
      joinedActivityIds: [],
      managedActivityIds: []
    },
    {
      id: '2024123456',
      name: '调试用户',
      role: '学生',
      password: 'student123',
      gender: '男',
      avatar: '/AnonymousAvatar.png',
      department: '计算机学院',
      phone: '13800000006',
      joinedActivityIds: [2],
      managedActivityIds: []
    },
    {
      id: '2024010123',
      name: '刘同学',
      role: '学生',
      password: 'student123',
      gender: '女',
      avatar: '/AnonymousAvatar.png',
      department: '外国语学院',
      phone: '13800000007',
      joinedActivityIds: [2],
      managedActivityIds: []
    },
    {
      id: '2024020789',
      name: '陈同学',
      role: '学生',
      password: 'student123',
      gender: '男',
      avatar: '/AnonymousAvatar.png',
      department: '经济学院',
      phone: '13800000008',
      joinedActivityIds: [2],
      managedActivityIds: []
    }
  ],

  /* ============ 信息审核记录 auditRecords ============ */
  auditRecords: [
    {
      applyId: 'AR-20260201-0001',
      userName: '调试用户',
      userId: '2024123456',
      applicantName: '调试用户',
      applicantId: '2024123456',
      changeType: '电话变更',
      before: '13800000006',
      after: '13900000006',
      submittedAt: '2026-02-01T10:12:00+08:00',
      status: '待审核',
      approvalComment: ''
    },
    {
      applyId: 'AR-20260202-0002',
      userName: '刘同学',
      userId: '2024010123',
      applicantName: '系统管理员',
      applicantId: '0001',
      changeType: '所属部门变更',
      before: '外国语学院',
      after: '计算机学院',
      submittedAt: '2026-02-02T14:30:00+08:00',
      status: '已同意',
      approvalComment: '材料齐全，同意变更。'
    },
    {
      applyId: 'AR-20260203-0003',
      userName: '陈同学',
      userId: '2024020789',
      applicantName: '陈同学',
      applicantId: '2024020789',
      changeType: '性别变更',
      before: '男',
      after: '女',
      submittedAt: '2026-02-03T09:05:00+08:00',
      status: '已拒绝',
      approvalComment: ''
    }
  ],

  /* ============ 请假审批记录 leaveApprovals ============ */
  leaveApprovals: [
    {
      applyId: 'LA-20260201-0001',

      activityId: 2,
      activityTitle: '校级程序设计竞赛',

      teacherId: '2023001',
      teacherName: '张老师',

      applicantId: '2024123456',
      applicantName: '调试用户',

      reason: '与学院安排的实验课时间冲突，申请请假。',
      submittedAt: '2026-02-01T11:20:00+08:00',

      status: '待审核',
      approvalComment: ''
    },
    {
      applyId: 'LA-20260202-0002',

      activityId: 2,
      activityTitle: '校级程序设计竞赛',

      teacherId: '2023001',
      teacherName: '张老师',

      applicantId: '2024010123',
      applicantName: '刘同学',

      reason: '当天需要参加外语学院组织的面试，时间冲突。',
      submittedAt: '2026-02-02T09:35:00+08:00',

      status: '已同意',
      approvalComment: '情况属实，已批准请假。'
    },
    {
      applyId: 'LA-20260203-0003',

      activityId: 2,
      activityTitle: '校级程序设计竞赛',

      teacherId: '2023001',
      teacherName: '张老师',

      applicantId: '2024020789',
      applicantName: '陈同学',

      reason: '个人原因无法到场。',
      submittedAt: '2026-02-03T16:10:00+08:00',

      status: '已拒绝',
      approvalComment: '理由不充分，请按要求参加活动。'
    }
  ],

  /* ============ 活动创建申请 activityCreateApprovals ============ */
  activityCreateApprovals: [
    {
      applyId: 'AC-20260206-0001',

      title: '数据结构算法训练营',
      organizer: '计算机学院',
      leaderId: '2023002',
      leaderName: '李老师',
      startAt: '2026-02-20T18:30:00+08:00',
      endAt: '2026-02-20T20:30:00+08:00',
      location: '信息楼201',
      summary: '面向大一大二，算法基础训练。',
      content: '包含基础题讲解、现场练习和答疑。',

      submittedAt: '2026-02-06T10:20:00+08:00',
      status: '待审核', 

      auditorId: '', 
      auditorName: '',
      auditedAt: '',
      comment: ''
    },

    {
      applyId: 'AC-20260201-0002',

      title: '英语角交流活动',
      organizer: '外国语学院',
      leaderId: '2023001',
      leaderName: '张老师',
      startAt: '2026-02-12T19:00:00+08:00',
      endAt: '2026-02-12T20:30:00+08:00',
      location: '图书馆一楼报告厅',
      summary: '自由交流+主题讨论，提升口语与表达。',
      content: '活动包含分组交流、主题讨论和老师点评，欢迎报名参与。',

      submittedAt: '2026-02-01T09:20:00+08:00',
      status: '已同意',

      auditorId: '0001',
      auditorName: '系统管理员',
      auditedAt: '2026-02-01T10:05:00+08:00',
      comment: '材料齐全，批准创建。'
    }
  ],

  /* ============ 社区帖子 communityPosts ============ */
  communityPosts: [
    {
      id: 'post-teacher-0001',
      author: {
        id: '2023001',
        name: '张老师',
        role: '教师',
        department: '计算机学院',
        avatar: '/AnonymousAvatar.png'
      },
      createdByUserId: '2023001',
      createdAt: '2026-02-06T09:15:00+08:00',
      type: 'activity_recommend',
      relatedActivityId: 1,
      content:
        '【活动推荐】人工智能前沿学术讲座来啦！本次会围绕大模型与智能系统的最新研究展开，也会有答疑环节。欢迎同学们来听～',
      images: ['/community/teacher1/1.jpg'],
      stats: { likes: 38, comments: 6, favorites: 12, views: 520 },
      userState: { liked: false, favorited: true },
      comments: [
        {
          id: 'c-t-0001',
          user: { id: '2024010123', name: '刘同学', role: '学生', avatar: '/AnonymousAvatar.png' },
          createdAt: '2026-02-06T09:40:00+08:00',
          content: '老师需要提前报名吗？',
          replies: [
            {
              id: 'r-t-0001',
              user: { id: '2023001', name: '张老师', role: '教师', avatar: '/AnonymousAvatar.png' },
              createdAt: '2026-02-06T09:48:00+08:00',
              content: '不需要报名，提前到场签到即可～'
            }
          ]
        }
      ]
    },
    {
      id: 'post-counselor-0001',
      author: {
        id: '2024001',
        name: '王辅导员',
        role: '辅导员',
        department: '学生工作处',
        avatar: '/AnonymousAvatar.png'
      },
      createdByUserId: '2024001',
      createdAt: '2026-02-05T16:20:00+08:00',
      type: 'work_study',
      relatedActivityId: '',
      content:
        '【勤工俭学招聘】图书馆与信息中心现开放若干勤工助学岗位：\n' +
        '1）图书整理/咨询引导\n' +
        '2）机房值班/设备登记\n' +
        '要求：认真负责、每周可固定排班。\n' +
        '报名方式：请到“个人中心-申请”提交材料，截止 2 月 10 日。',
      images: [],
      stats: { likes: 15, comments: 4, favorites: 9, views: 310 },
      userState: { liked: false, favorited: false },
      comments: [
        {
          id: 'c-w-0001',
          user: { id: '2024123456', name: '调试用户', role: '学生', avatar: '/profilePicture.jpg' },
          createdAt: '2026-02-05T16:45:00+08:00',
          content: '请问每周大概需要排班几次？',
          replies: [
            {
              id: 'r-w-0001',
              user: { id: '2024001', name: '王辅导员', role: '辅导员', avatar: '/AnonymousAvatar.png' },
              createdAt: '2026-02-05T16:55:00+08:00',
              content: '一般每周 2-3 次，每次 2 小时左右，会根据课表协调。'
            }
          ]
        }
      ]
    },
    {
      id: 'post-student-0001',
      author: {
        id: '2024123456',
        name: '调试用户',
        role: '学生',
        department: '计算机学院',
        avatar: '/profilePicture.jpg'
      },
      createdByUserId: '2024123456',
      createdAt: '2026-02-04T21:10:00+08:00',
      type: 'activity_recommend',
      relatedActivityId: 2,
      content:
        '【活动推荐】校级程序设计竞赛真的很值得参加！我把报名流程和准备资料整理成九宫格了，想参赛的同学可以参考～一起冲！',
      images: [
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg',
        '/community/student1/1.jpg'
      ],
      stats: { likes: 56, comments: 10, favorites: 22, views: 860 },
      userState: { liked: true, favorited: true },
      comments: [
        {
          id: 'c-s-0001',
          user: { id: '2023002', name: '李老师', role: '教师', avatar: '/AnonymousAvatar.png' },
          createdAt: '2026-02-04T21:40:00+08:00',
          content: '整理得很细致，祝你比赛取得好成绩！',
          replies: [
            {
              id: 'r-s-0001',
              user: { id: '2024123456', name: '调试用户', role: '学生', avatar: '/profilePicture.jpg' },
              createdAt: '2026-02-04T21:45:00+08:00',
              content: '谢谢老师！我会继续加油的！'
            }
          ]
        }
      ]
    }
  ],
  
  /* ============ 当前激活的消息会话 activeMessageId ============ */
  activeMessageId: ''
} as const
