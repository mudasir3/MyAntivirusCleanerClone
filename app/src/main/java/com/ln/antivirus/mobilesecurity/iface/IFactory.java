package com.ln.antivirus.mobilesecurity.iface;

public interface IFactory<T> {
    T createInstance(String str);
}
