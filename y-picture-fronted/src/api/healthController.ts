// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** ok GET /api/health */
export async function okUsingGet(options?: { [key: string]: any }) {
  return request<string>('/api/health', {
    method: 'GET',
    ...(options || {}),
  })
}
