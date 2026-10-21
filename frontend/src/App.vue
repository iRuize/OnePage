<template>
    <router-view />

    <div class="toast-wrap">
        <transition-group name="toast">
            <div v-for="toast in toasts" :key="toast.id" class="toast" :class="toast.type">
                <span class="dot"></span>
                <span>{{ toast.message }}</span>
            </div>
        </transition-group>
    </div>
</template>

<script setup>
import { useToast } from './composables/useToast'

const { toasts } = useToast()
</script>
