// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** addSpaceUser POST /api/spaceUser/add */
export async function addSpaceUserUsingPost(
  body: API.SpaceUserAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/spaceUser/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** deleteSpaceUser POST /api/spaceUser/delete */
export async function deleteSpaceUserUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteSpaceUserUsingPOSTParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/spaceUser/delete', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** editSpaceUser POST /api/spaceUser/edit */
export async function editSpaceUserUsingPost(
  body: API.SpaceUserEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/spaceUser/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listSpaceUser POST /api/spaceUser/list */
export async function listSpaceUserUsingPost(
  body: API.SpaceUserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListSpaceUserVO_>('/api/spaceUser/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listMyTeamSpace POST /api/spaceUser/list/my */
export async function listMyTeamSpaceUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseListSpaceUserVO_>('/api/spaceUser/list/my', {
    method: 'POST',
    ...(options || {}),
  })
}
