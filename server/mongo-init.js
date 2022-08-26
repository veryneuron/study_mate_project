db.createUser(
    {
        user: "jaemin",
        pwd: "jaemin",
        roles: [
            {
                role: "readWrite",
                db: "study_mate"
            }
        ]
    }
);