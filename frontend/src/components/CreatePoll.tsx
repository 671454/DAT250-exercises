import { type FormEvent, useState } from "react";

type Poll = {
    id: number;
    question: string;
    publishedAt: string;
    validUntil: string;
    options: string[];
};

export function CreatePoll() {
    const [question, setQuestion] = useState("");
    const [validUntil, setValidUntil] = useState("");
    const [options, setOptions] = useState<string[]>(["", ""]);

    function handleOptionChange(idx: number, value: string) {
        setOptions(prev => prev.map((opt, i) => (i === idx ? value : opt)));
    }

    function addOption() {
        setOptions(prev => [...prev, ""]);
    }

    function removeOption(idx: number) {
        setOptions(prev => prev.filter((_, i) => i !== idx));
    }

    function handleSubmit(e: FormEvent) {
        e.preventDefault();

        const trimmed = options.map(o => o.trim()).filter(Boolean);
        if (!question.trim() || trimmed.length < 2) {
            alert("Please enter a question and at least two options.");
            return;
        }
        if (validUntil && new Date(validUntil) < new Date()) {
            alert("Valid until must be in the future.");
            return;
        }

        const poll: Poll = {
            id: Date.now(),
            question: question.trim(),
            publishedAt: new Date().toISOString(), // alltid “nå”
            validUntil,
            options: trimmed,
        };

        console.log("poll", poll);
        // TODO: send til backend senere
    }

    const isValid =
        question.trim().length > 0 &&
        options.filter(o => o.trim().length > 0).length >= 2 &&
        (!validUntil || new Date(validUntil) >= new Date());

    return (
        <form onSubmit={handleSubmit} className="flex flex-col gap-3 max-w-lg">
            <label htmlFor="q" className="font-medium">Question</label>
            <input
                id="q"
                className="border rounded p-2"
                value={question}
                onChange={e => setQuestion(e.target.value)}
                placeholder="What should we...?"
            />

            <div>
                <label htmlFor="until" className="font-medium">Valid until</label>
                <input
                    id="until"
                    type="datetime-local"
                    className="border rounded p-2 w-full"
                    value={validUntil}
                    min={new Date().toISOString().slice(0,16)} // hindrer å velge fortid
                    onChange={e => setValidUntil(e.target.value)}
                />
            </div>

            <div className="space-y-2">
                <div className="font-medium">Options</div>
                {options.map((opt, idx) => (
                    <div key={idx} className="flex gap-2">
                        <input
                            className="border rounded p-2 flex-1"
                            value={opt}
                            onChange={e => handleOptionChange(idx, e.target.value)}
                            placeholder={`Option ${idx + 1}`}
                        />
                        {options.length > 2 && (
                            <button
                                type="button"
                                className="border rounded px-2"
                                onClick={() => removeOption(idx)}
                            >
                                Remove
                            </button>
                        )}
                    </div>
                ))}
                <button type="button" className="border rounded px-3 py-2 w-fit" onClick={addOption}>
                    + Add option
                </button>
            </div>

            <button
                type="submit"
                disabled={!isValid}
                className="bg-blue-600 disabled:bg-blue-300 text-white rounded px-4 py-2 w-fit"
            >
                Publish poll
            </button>
        </form>
    );
}
