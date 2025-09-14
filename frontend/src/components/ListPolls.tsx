import { useEffect, useState } from "react";
import { MockApi, type Poll } from "../services/mockApi";

export function ListPolls() {
    const [polls, setPolls] = useState<Poll[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]   = useState<string | null>(null);

    useEffect(() => {
        let alive = true;
        (async () => {
            try {
                const data = await MockApi();
                if (alive) setPolls(data);
            } catch (e) {
                if (alive) setError("Kunne ikke hente polls");
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => { alive = false; };
    }, []);

    if (loading) return <p>Laster…</p>;
    if (error)   return <p className="text-red-600">{error}</p>;
    if (polls.length === 0) return <p>Ingen polls enda.</p>;

    return (
        <ul className="space-y-2">
            {polls.map(p => (
                <li key={p.id} className="border rounded p-3">
                    <div className="flex items-center justify-between">
                        <h3 className="font-semibold">{p.question}</h3>
                        <span className="text-xs text-gray-500">
              gyldig til {new Date(p.validUntil).toLocaleString()}
            </span>
                    </div>
                    <div className="text-sm text-gray-600">
                        {p.options.map(o => o.label).join(" / ")}
                    </div>
                    {/* Senere: link/knapp "Stem" som åpner detaljvisning */}
                </li>
            ))}
        </ul>
    );
}
