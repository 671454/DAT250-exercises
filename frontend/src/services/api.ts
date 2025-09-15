export type VoteOption = {
    id: number
    caption: string
    presentationOrder: number
}
export type Vote = {
    id: number
    publishedAt: string
    voter: number | User // <--- cause of @JsonIdentityReference(alwaysAsId = true), this might be a number and not an object
    poll: number | Poll
    option: number | VoteOption
}
export type Poll = {
    id: number
    question: string
    publishedAt: string
    validUntil: string | null
    creator: number | User
    voteOptions: number[] | VoteOption[]
    votes: number[] | Vote[]
}
export type User = {
    id: number
    username: string
    email: string
    pollsCreated: number[] | Poll[]
    votes: number[] | Vote[]
}

export type PollResult = {optionId : number, caption : string, count : number}

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'


async function json<T>(res: Response): Promise<T> {
    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`${res.status} ${res.statusText}${text ? ` – ${text}` : ""}`);
    }
    return res.json() as Promise<T>;
}


export async function listPolls(): Promise<Poll[]> {
    const res = await fetch(`${BASE_URL}/polls`)
    return json<Poll[]>(res)
}

export async function getPoll(id: number): Promise<Poll> {
    const res = await fetch(`${BASE_URL}/polls/${id}`)
    return json<Poll>(res)
}

export async function createPoll(creatorId: number, question: string, options: string[], validUntil?: string)  {
    const res = await fetch(`${BASE_URL}/polls`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({creatorId, question, options, validUntil : validUntil?.trim() ? validUntil : null})
    })
    return json<{pollId : number}>(res)
}

export async function vote(pollId: number, userId: number, optionId: number, when? : string) {
    const res = await fetch(`${BASE_URL}/polls/${pollId}/vote`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({userId, optionId, when})
    })
    return json<{voteId : number}>(res)
}

export async function createUser(username: string, email: string){
    const res = await fetch(`${BASE_URL}/users`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username, email})
    })
    return json<User>(res)
}
export async function getUser(id: number){
    const res = await fetch(`${BASE_URL}/users/${id}`)
    return json<User>(res)
}

export async function getPollResults(pollId: number){
    const res = await fetch(`${BASE_URL}/polls/${pollId}/results`, {})
    return json<PollResult[]>(res)
}



