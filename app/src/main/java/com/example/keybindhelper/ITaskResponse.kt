package com.example.keybindhelper

interface ITaskResponse<T> {
    fun onResponse(result: T);
}