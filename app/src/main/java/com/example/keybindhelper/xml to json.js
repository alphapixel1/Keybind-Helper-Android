//xml to json how to use
//step 1, remove the spacings in the encoding
//step 2 open in firefox and run this in console
var name=document.querySelector("Project").getAttribute("Name")
var ret={
    "projectName":name,
    "groups": []
}
ret.groups=[...document.querySelectorAll("Group")].map(group=>{
    return { 
        "groupName": group.querySelector("Name").innerHTML,
        "keybinds":[...group.querySelectorAll("Keybind")].map(keybind=>{
            return {
                "keybindName":keybind.getAttribute("Name"),
                "keybinds":[keybind.getAttribute("Bind1"),keybind.getAttribute("Bind2"),keybind.getAttribute("Bind3")].filter(z=>z.length>0)
            }
        })
    }
})
console.log(ret);
console.log(JSON.stringify(ret));