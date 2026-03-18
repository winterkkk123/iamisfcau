# API Endpoints

- Export time: 2026-03-04T00:15:16.150258400
- Count: 25

## GET /api/activities

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#list`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityDto>`

### Params

- (none)

## GET /api/activities/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#detail`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.Integer` | true | - |

## GET /api/activities/{id}/attendance

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#attendance`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto>`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.Integer` | true | - |

## GET /api/activity-create-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#list`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto>`

### Params

- (none)

## POST /api/activity-create-approvals/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#approve`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## POST /api/activity-create-approvals/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#reject`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## GET /api/ai/conversations

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#conversations`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiConversationDto>`

### Params

- (none)

## GET /api/ai/conversations/{id}/messages

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#messages`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiMessageDto>`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |

## POST /api/auth/login

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AuthController#login`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LoginResponse`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.LoginRequest` | true | - |

## POST /api/community/comments/{commentId}/replies

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#reply`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest` | true | - |

## GET /api/community/posts

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#listPosts`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto>`

### Params

- (none)

## GET /api/community/posts/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#detail`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| query | (unknown) | `boolean` | true | true |

## POST /api/community/posts/{id}/comments

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#createComment`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest` | true | - |

## POST /api/community/posts/{id}/favorite

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#toggleFavorite`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |

## POST /api/community/posts/{id}/like

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#toggleLike`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |

## GET /api/leave-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#list`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto>`

### Params

- (none)

## POST /api/leave-approvals/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#approve`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## POST /api/leave-approvals/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#reject`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## GET /api/messages/conversations

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController#conversations`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageConversationDto>`

### Params

- (none)

## GET /api/messages/conversations/{id}/messages

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController#messages`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageDto>`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |

## GET /api/notices

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.NoticeController#list`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.NoticeDto>`

### Params

- (none)

## GET /api/profile-change-applications

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#list`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto>`

### Params

- (none)

## POST /api/profile-change-applications/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#approve`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## POST /api/profile-change-applications/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#reject`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto`

### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | (unknown) | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

## GET /api/users/me

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#me`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.UserMeDto`

### Params

- (none)

