import {useEffect, useState} from "react";
import {
    getPollResults,
    getUser,
    listPolls,
    type Poll,
    type PollResult,
    type User, vote
} from "../services/api";

type PollItem = {
    id: number;
    question: string;
    creatorName: string;
    results: PollResult[];
}


interface ListPollsProps {
    currentUserId: number
    onBack?: () => void
}

export function ListPolls({currentUserId, onBack}: ListPollsProps) {
    const [polls, setPolls] = useState<PollItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        console.log('BASE_URL =', import.meta.env.VITE_API_BASE_URL);
        let alive = true;
        (async () => {
            try {
                const data = await listPolls();

                const enriched = await Promise.all(
                    data.map(async (p: Poll): Promise<PollItem> => {
                        let creatorName = "Unknown";

                        if (typeof p.creator === "number") {
                            const u: User = await getUser(p.creator); // <-- bruk creator-id
                            creatorName = u.username;
                        } else if (p.creator && "username" in p.creator) {
                            creatorName = p.creator.username;
                        }

                        const results = await getPollResults(p.id);
                        return {id: p.id, question: p.question, creatorName, results};
                    })
                );

                if (alive) setPolls(enriched);
            } catch (e) {
                if (alive) setError("Unable to retrieve polls" + String(e));
            } finally {
                if (alive) setLoading(false);
            }
        })();

        return () => {
            alive = false;
        };
    }, []);

    async function handleVote(pollId: number, optionId: number) {
        await vote(pollId, currentUserId, optionId, new Date().toISOString());
        setPolls(prev => prev.map(p => p.id !== pollId ? p : {...p, results: []}))
        const fresh =  await getPollResults(pollId);

        setPolls(prev => prev.map(p => p.id !== pollId ? p : {...p, results: fresh}))
    }


    if (loading) return <p>Loading…</p>;
    if (error) return <p className="text-red-600">{error}</p>;
    if (polls.length === 0) return <p>No polls yet</p>;

    return (
        <div className="space-y-4">

            <div className={"space-y-4"}>
                <button type={"button"} className={"border rounded px-3 py-2"} onClick={() => onBack?.()}>Home</button>
            </div>

            {polls.map(p => {
                const total = p.results.reduce((s, r) => s + Number(r.count), 0) || 1;
                return (
                    <div key={p.id} className="border rounded p-4">
                        <div className="flex items-center justify-between">
                            <h3 className="text-lg font-semibold">{p.question}</h3>
                            <span className="text-sm text-gray-600">Opprettet av {p.creatorName}</span>
                        </div>
                        <div className="mt-3 space-y-2">
                            {p.results.map(r => {
                                const pct = Math.round((Number(r.count) / total) * 100);
                                return (
                                    <div key={r.optionId}>
                                        <div className="flex justify-between text-sm">
                                            <span>{r.caption}</span>
                                            <span className="tabular-nums">{r.count} ({pct}%)</span>
                                        </div>
                                        <div className="h-2 bg-gray-200 rounded">
                                            <div className="h-2 bg-blue-600 rounded" style={{width: `${pct}%`}}/>
                                        </div>
                                        <button className={"mt-1 border rounded px-2 py-1 text-sm"} onClick={() => handleVote(p.id, r.optionId)}>
                                            Vote on "{r.caption}"
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}
