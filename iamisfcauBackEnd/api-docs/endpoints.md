# API Endpoints

- Export time: 2026-03-16T22:21:39.718495200
- Count: 44
- Base URL: http://localhost:8080

## Overview

- ActivityController (7)
- ActivityCreateApprovalController (5)
- AiController (4)
- AuthController (1)
- CommunityController (8)
- LeaveApprovalController (5)
- MessageCenterController (3)
- NoticeController (2)
- ProfileChangeController (4)
- UserController (5)

## Controller: ActivityController

| Method | Path | Handler |
|---|---|---|
| GET | /api/activities | `list` |
| GET | /api/activities/joined | `joined` |
| GET | /api/activities/{id} | `detail` |
| GET | /api/activities/{id}/attendance | `attendance` |
| PUT | /api/activities/{id}/attendance/{userId} | `updateAttendance` |
| DELETE | /api/activities/{id}/join | `cancelJoin` |
| POST | /api/activities/{id}/join | `join` |

### GET /api/activities

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityDto> cn.edu.hdu.iamisfcaubackend.controller.ActivityController.list()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/activities"
```

### GET /api/activities/joined

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#joined`
- Java: `public java.util.List<java.lang.Integer> cn.edu.hdu.iamisfcaubackend.controller.ActivityController.joined(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `java.util.List<java.lang.Integer>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| query | userId | `java.lang.String` | true | 
		
		

				
 |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/activities/joined"
```

### GET /api/activities/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#detail`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityDto cn.edu.hdu.iamisfcaubackend.controller.ActivityController.detail(java.lang.Integer)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/activities/{id}"
```

### GET /api/activities/{id}/attendance

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#attendance`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto> cn.edu.hdu.iamisfcaubackend.controller.ActivityController.attendance(java.lang.Integer)`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/activities/{id}/attendance"
```

### PUT /api/activities/{id}/attendance/{userId}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#updateAttendance`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto cn.edu.hdu.iamisfcaubackend.controller.ActivityController.updateAttendance(java.lang.Integer,java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.AttendanceUpdateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.AttendanceDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |
| path | userId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.AttendanceUpdateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.AttendanceUpdateRequest)

```json
{
  "attendStatus": "string",
  "isLate": true
}
```

#### cURL

```bash
curl -X PUT "http://localhost:8080/api/activities/{id}/attendance/{userId}" \
  -H "Content-Type: application/json" \
  -d '{   "attendStatus": "string",   "isLate": true }'
```

### DELETE /api/activities/{id}/join

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#cancelJoin`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinResponse cn.edu.hdu.iamisfcaubackend.controller.ActivityController.cancelJoin(java.lang.Integer,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinResponse`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |
| query | userId | `java.lang.String` | true | 
		
		

				
 |

#### cURL

```bash
curl -X DELETE "http://localhost:8080/api/activities/{id}/join"
```

### POST /api/activities/{id}/join

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityController#join`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinResponse cn.edu.hdu.iamisfcaubackend.controller.ActivityController.join(java.lang.Integer,cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinResponse`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ActivityJoinRequest)

```json
{
  "userId": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/activities/{id}/join" \
  -H "Content-Type: application/json" \
  -d '{   "userId": "string" }'
```

## Controller: ActivityCreateApprovalController

| Method | Path | Handler |
|---|---|---|
| GET | /api/activity-create-approvals | `list` |
| POST | /api/activity-create-approvals | `create` |
| DELETE | /api/activity-create-approvals/{applyId} | `withdraw` |
| POST | /api/activity-create-approvals/{applyId}/approve | `approve` |
| POST | /api/activity-create-approvals/{applyId}/reject | `reject` |

### GET /api/activity-create-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto> cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController.list()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/activity-create-approvals"
```

### POST /api/activity-create-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#create`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController.create(cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApplyRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApplyRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApplyRequest)

```json
{
  "title": "string",
  "organizer": "string",
  "leaderId": "string",
  "startAt": "string",
  "endAt": "string",
  "location": "string",
  "summary": "string",
  "content": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/activity-create-approvals" \
  -H "Content-Type: application/json" \
  -d '{   "title": "string",   "organizer": "string",   "leaderId": "string",   "startAt": "string",   "endAt": "string",   "location": "string",   "summary": "string",   "content": "string" }'
```

### DELETE /api/activity-create-approvals/{applyId}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#withdraw`
- Java: `public void cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController.withdraw(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `void`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |

#### cURL

```bash
curl -X DELETE "http://localhost:8080/api/activity-create-approvals/{applyId}"
```

### POST /api/activity-create-approvals/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#approve`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController.approve(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/activity-create-approvals/{applyId}/approve" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

### POST /api/activity-create-approvals/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController#reject`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto cn.edu.hdu.iamisfcaubackend.controller.ActivityCreateApprovalController.reject(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ActivityCreateApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/activity-create-approvals/{applyId}/reject" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

## Controller: AiController

| Method | Path | Handler |
|---|---|---|
| POST | /api/ai/chat | `chat` |
| GET | /api/ai/conversations | `conversations` |
| POST | /api/ai/conversations | `createConversation` |
| GET | /api/ai/conversations/{id}/messages | `messages` |

### POST /api/ai/chat

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#chat`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.AiChatResponse cn.edu.hdu.iamisfcaubackend.controller.AiController.chat(cn.edu.hdu.iamisfcaubackend.dto.AiChatRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.AiChatResponse`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.AiChatRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.AiChatRequest)

```json
{
  "conversationId": "string",
  "message": "string",
  "userId": "string",
  "userName": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/ai/chat" \
  -H "Content-Type: application/json" \
  -d '{   "conversationId": "string",   "message": "string",   "userId": "string",   "userName": "string" }'
```

### GET /api/ai/conversations

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#conversations`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiConversationSummaryDto> cn.edu.hdu.iamisfcaubackend.controller.AiController.conversations()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiConversationSummaryDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/ai/conversations"
```

### POST /api/ai/conversations

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#createConversation`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.AiConversationDto cn.edu.hdu.iamisfcaubackend.controller.AiController.createConversation()`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.AiConversationDto`

#### Params

- (none)

#### cURL

```bash
curl -X POST "http://localhost:8080/api/ai/conversations"
```

### GET /api/ai/conversations/{id}/messages

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AiController#messages`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiMessageDto> cn.edu.hdu.iamisfcaubackend.controller.AiController.messages(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.AiMessageDto>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/ai/conversations/{id}/messages"
```

## Controller: AuthController

| Method | Path | Handler |
|---|---|---|
| POST | /api/auth/login | `login` |

### POST /api/auth/login

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.AuthController#login`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.UserMeDto cn.edu.hdu.iamisfcaubackend.controller.AuthController.login(cn.edu.hdu.iamisfcaubackend.dto.LoginRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.UserMeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.LoginRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.LoginRequest)

```json
{
  "id": "string",
  "password": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{   "id": "string",   "password": "string" }'
```

## Controller: CommunityController

| Method | Path | Handler |
|---|---|---|
| POST | /api/community/comments/{commentId}/replies | `reply` |
| GET | /api/community/posts | `listPosts` |
| POST | /api/community/posts | `createPost` |
| DELETE | /api/community/posts/{id} | `deletePost` |
| GET | /api/community/posts/{id} | `detail` |
| POST | /api/community/posts/{id}/comments | `createComment` |
| POST | /api/community/posts/{id}/favorite | `toggleFavorite` |
| POST | /api/community/posts/{id}/like | `toggleLike` |

### POST /api/community/comments/{commentId}/replies

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#reply`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.reply(java.lang.String,java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | commentId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest)

```json
{
  "content": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/community/comments/{commentId}/replies" \
  -H "Content-Type: application/json" \
  -d '{   "content": "string" }'
```

### GET /api/community/posts

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#listPosts`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto> cn.edu.hdu.iamisfcaubackend.controller.CommunityController.listPosts(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/community/posts"
```

### POST /api/community/posts

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#createPost`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.createPost(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.CommunityPostCreateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CommunityPostCreateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.CommunityPostCreateRequest)

```json
{
  "content": "string",
  "type": "string",
  "relatedActivityId": "string",
  "images": [
    "item"
  ]
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/community/posts" \
  -H "Content-Type: application/json" \
  -d '{   "content": "string",   "type": "string",   "relatedActivityId": "string",   "images": [     "item"   ] }'
```

### DELETE /api/community/posts/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#deletePost`
- Java: `public void cn.edu.hdu.iamisfcaubackend.controller.CommunityController.deletePost(java.lang.String,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `void`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X DELETE "http://localhost:8080/api/community/posts/{id}"
```

### GET /api/community/posts/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#detail`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.detail(java.lang.String,java.lang.String,boolean)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityPostDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | id | `java.lang.String` | true | - |
| query | incViews | `boolean` | true | true |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/community/posts/{id}"
```

### POST /api/community/posts/{id}/comments

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#createComment`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.createComment(java.lang.String,java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.CommunityCommentDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | id | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.CommentCreateRequest)

```json
{
  "content": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/community/posts/{id}/comments" \
  -H "Content-Type: application/json" \
  -d '{   "content": "string" }'
```

### POST /api/community/posts/{id}/favorite

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#toggleFavorite`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.toggleFavorite(java.lang.String,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X POST "http://localhost:8080/api/community/posts/{id}/favorite"
```

### POST /api/community/posts/{id}/like

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.CommunityController#toggleLike`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto cn.edu.hdu.iamisfcaubackend.controller.CommunityController.toggleLike(java.lang.String,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ReactionUpdateDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X POST "http://localhost:8080/api/community/posts/{id}/like"
```

## Controller: LeaveApprovalController

| Method | Path | Handler |
|---|---|---|
| GET | /api/leave-approvals | `list` |
| POST | /api/leave-approvals | `create` |
| DELETE | /api/leave-approvals/{applyId} | `withdraw` |
| POST | /api/leave-approvals/{applyId}/approve | `approve` |
| POST | /api/leave-approvals/{applyId}/reject | `reject` |

### GET /api/leave-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto> cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController.list()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/leave-approvals"
```

### POST /api/leave-approvals

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#create`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController.create(cn.edu.hdu.iamisfcaubackend.dto.LeaveApplyCreateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.LeaveApplyCreateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.LeaveApplyCreateRequest)

```json
{
  "activityId": 123,
  "applicantId": "string",
  "reason": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/leave-approvals" \
  -H "Content-Type: application/json" \
  -d '{   "activityId": 123,   "applicantId": "string",   "reason": "string" }'
```

### DELETE /api/leave-approvals/{applyId}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#withdraw`
- Java: `public void cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController.withdraw(java.lang.String,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `void`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| query | applicantId | `java.lang.String` | true | 
		
		

				
 |

#### cURL

```bash
curl -X DELETE "http://localhost:8080/api/leave-approvals/{applyId}"
```

### POST /api/leave-approvals/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#approve`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController.approve(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/leave-approvals/{applyId}/approve" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

### POST /api/leave-approvals/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController#reject`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto cn.edu.hdu.iamisfcaubackend.controller.LeaveApprovalController.reject(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.LeaveApprovalDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/leave-approvals/{applyId}/reject" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

## Controller: MessageCenterController

| Method | Path | Handler |
|---|---|---|
| GET | /api/messages/conversations | `conversations` |
| GET | /api/messages/conversations/{id}/messages | `messages` |
| POST | /api/messages/conversations/{id}/messages | `sendMessage` |

### GET /api/messages/conversations

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController#conversations`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageConversationDto> cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController.conversations()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageConversationDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/messages/conversations"
```

### GET /api/messages/conversations/{id}/messages

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController#messages`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageDto> cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController.messages(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.MessageDto>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/messages/conversations/{id}/messages"
```

### POST /api/messages/conversations/{id}/messages

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController#sendMessage`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.MessageDto cn.edu.hdu.iamisfcaubackend.controller.MessageCenterController.sendMessage(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.MessageSendRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.MessageDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.MessageSendRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.MessageSendRequest)

```json
{
  "role": "string",
  "content": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/messages/conversations/{id}/messages" \
  -H "Content-Type: application/json" \
  -d '{   "role": "string",   "content": "string" }'
```

## Controller: NoticeController

| Method | Path | Handler |
|---|---|---|
| GET | /api/notices | `list` |
| GET | /api/notices/{id} | `detail` |

### GET /api/notices

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.NoticeController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.NoticeDto> cn.edu.hdu.iamisfcaubackend.controller.NoticeController.list()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.NoticeDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/notices"
```

### GET /api/notices/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.NoticeController#detail`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.NoticeDto cn.edu.hdu.iamisfcaubackend.controller.NoticeController.detail(java.lang.Integer)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.NoticeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.Integer` | true | - |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/notices/{id}"
```

## Controller: ProfileChangeController

| Method | Path | Handler |
|---|---|---|
| GET | /api/profile-change-applications | `list` |
| POST | /api/profile-change-applications | `create` |
| POST | /api/profile-change-applications/{applyId}/approve | `approve` |
| POST | /api/profile-change-applications/{applyId}/reject | `reject` |

### GET /api/profile-change-applications

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto> cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController.list()`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto>`

#### Params

- (none)

#### cURL

```bash
curl -X GET "http://localhost:8080/api/profile-change-applications"
```

### POST /api/profile-change-applications

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#create`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController.create(cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeCreateRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeCreateRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeCreateRequest)

```json
{
  "targetUserId": "string",
  "applicantId": "string",
  "changeType": "string",
  "beforeValue": "string",
  "afterValue": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/profile-change-applications" \
  -H "Content-Type: application/json" \
  -d '{   "targetUserId": "string",   "applicantId": "string",   "changeType": "string",   "beforeValue": "string",   "afterValue": "string" }'
```

### POST /api/profile-change-applications/{applyId}/approve

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#approve`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController.approve(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/profile-change-applications/{applyId}/approve" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

### POST /api/profile-change-applications/{applyId}/reject

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController#reject`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto cn.edu.hdu.iamisfcaubackend.controller.ProfileChangeController.reject(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | applyId | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest` | false | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.ApprovalRequest)

```json
{
  "approvalComment": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/profile-change-applications/{applyId}/reject" \
  -H "Content-Type: application/json" \
  -d '{   "approvalComment": "string" }'
```

## Controller: UserController

| Method | Path | Handler |
|---|---|---|
| GET | /api/users | `list` |
| POST | /api/users | `create` |
| GET | /api/users/me | `me` |
| DELETE | /api/users/{id} | `delete` |
| PUT | /api/users/{id} | `update` |

### GET /api/users

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#list`
- Java: `public java.util.List<cn.edu.hdu.iamisfcaubackend.dto.UserDto> cn.edu.hdu.iamisfcaubackend.controller.UserController.list(java.lang.String,java.lang.String)`
- Produces: -
- Consumes: -
- Return: `java.util.List<cn.edu.hdu.iamisfcaubackend.dto.UserDto>`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| query | name | `java.lang.String` | false | 
		
		

				
 |
| query | role | `java.lang.String` | false | 
		
		

				
 |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/users"
```

### POST /api/users

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#create`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.UserDto cn.edu.hdu.iamisfcaubackend.controller.UserController.create(cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.UserDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest)

```json
{
  "id": "string",
  "name": "string",
  "role": "string",
  "password": "string",
  "gender": "string",
  "avatar": "string",
  "department": "string",
  "phone": "string"
}
```

#### cURL

```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{   "id": "string",   "name": "string",   "role": "string",   "password": "string",   "gender": "string",   "avatar": "string",   "department": "string",   "phone": "string" }'
```

### GET /api/users/me

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#me`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.UserMeDto cn.edu.hdu.iamisfcaubackend.controller.UserController.me(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.UserMeDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| header | X-User-Id | `java.lang.String` | true | 
		
		

				
 |

#### cURL

```bash
curl -X GET "http://localhost:8080/api/users/me"
```

### DELETE /api/users/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#delete`
- Java: `public void cn.edu.hdu.iamisfcaubackend.controller.UserController.delete(java.lang.String)`
- Produces: -
- Consumes: -
- Return: `void`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.String` | true | - |

#### cURL

```bash
curl -X DELETE "http://localhost:8080/api/users/{id}"
```

### PUT /api/users/{id}

- Handler: `cn.edu.hdu.iamisfcaubackend.controller.UserController#update`
- Java: `public cn.edu.hdu.iamisfcaubackend.dto.UserDto cn.edu.hdu.iamisfcaubackend.controller.UserController.update(java.lang.String,cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest)`
- Produces: -
- Consumes: -
- Return: `cn.edu.hdu.iamisfcaubackend.dto.UserDto`

#### Params

| In | Name | Type | Required | Default |
|---|---|---|---:|---|
| path | id | `java.lang.String` | true | - |
| body | (requestBody) | `cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest` | true | - |

#### Request Body Example (cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest)

```json
{
  "name": "string",
  "role": "string",
  "password": "string",
  "gender": "string",
  "avatar": "string",
  "department": "string",
  "phone": "string"
}
```

#### cURL

```bash
curl -X PUT "http://localhost:8080/api/users/{id}" \
  -H "Content-Type: application/json" \
  -d '{   "name": "string",   "role": "string",   "password": "string",   "gender": "string",   "avatar": "string",   "department": "string",   "phone": "string" }'
```

