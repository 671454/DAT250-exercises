
export type VoteOption = {id: string, label: string}
export type Poll = {
    id: number
    question: string
    publishedAt: string
    validUntil: string
    options: VoteOption[]
}

export async function MockApi(): Promise<Poll[]> {
    await new Promise(r => setTimeout(r, 500))
    return [
        {
            id: 1,
            question: "Pizza eller taco?",
            publishedAt: new Date(Date.now() - 3600e3).toISOString(),
            validUntil: new Date(Date.now() + 24*3600e3).toISOString(),
            options: [{ id: "a", label: "Pizza" }, { id: "b", label: "Taco" }],
        },
        {
            id: 2,
            question: "Torsdag eller fredag?",
            publishedAt: new Date().toISOString(),
            validUntil: new Date(Date.now() + 48*3600e3).toISOString(),
            options: [{ id: "a", label: "Torsdag" }, { id: "b", label: "Fredag" }],
        },
    ];
}