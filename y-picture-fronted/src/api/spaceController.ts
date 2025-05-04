// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** getUserSpace GET /api/space */
export async function getUserSpaceUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/space', {
    method: 'GET',
    ...(options || {}),
  })
}

/** spaceAdd POST /api/space/add */
export async function spaceAddUsingPost(
  body: API.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/space/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getUserSpaceById GET /api/space/get/vo */
export async function getUserSpaceByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserSpaceByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSpaceVO_>('/api/space/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
