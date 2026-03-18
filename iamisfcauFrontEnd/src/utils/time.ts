// src/utils/time.ts

/** ISO -> YYYY-MM-DD */
export const ymd = (iso?: string) => (iso ? iso.slice(0, 10) : '')

/** ISO -> MM-DD */
export const md = (iso?: string) => (iso ? iso.slice(5, 10) : '')

/** ISO -> HH:mm */
export const hm = (iso?: string) => (iso ? iso.slice(11, 16) : '')

/**
 * 活动：date(ISO的 00:00:00) + time(HH:mm) -> "YYYY-MM-DD HH:mm"
 * 兼容你现在活动仍然是 date + time 分开的写法
 */
export const ymdHmFromDateTime = (dateIso?: string, time?: string) => {
  const d = ymd(dateIso)
  return d && time ? `${d} ${time}` : d || ''
}

/** ISO -> YYYY-MM-DD HH:mm */
export const ymdHm = (iso?: string) => {
  const d = ymd(iso)
  const t = hm(iso)
  return d && t ? `${d} ${t}` : d || ''
}