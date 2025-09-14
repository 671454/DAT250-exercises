import * as React from "react";

export function Identify() {
    const [name, setName] = React.useState<string>('');
    const [email, setEmail] = React.useState<string>('');


    function handleSubmit(e: React.FormEvent) {
        e.preventDefault(); // For at siden ikke skal refreshe
        console.log(email);
        console.log(name);

        if(!name || !email) {
            alert("Please enter a valid input");
            return;
        }

    }

    return (
        <>
            <h1 className="text-3xl font-bold text-blue-600">Logg inn</h1>
            <form className="flex flex-col gap-2" onSubmit={handleSubmit}>
                <label htmlFor="name">Name</label>
                <input
                    type="text"
                    name="name"
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)} />
                <label htmlFor="email">Email</label>
                <input
                    type="text"
                    name="email"
                    id="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}/>
                <input type="submit" value="Submit" className={"bg-blue-600"}/>
            </form>
        </>
    );
}