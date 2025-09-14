export function Identify() {
    return (
        <>
            <h1 className="text-3xl font-bold text-blue-600">Logg inn test</h1>
            <form className="flex flex-col gap-2">
                <label htmlFor="name">Name</label>
                <input type="text" name="name" id="name" />
                <label htmlFor="email">Email</label>
                <input type="text" name="email" id="email" />
                <input type="submit" value="Submit" />
            </form>
        </>
    );
}