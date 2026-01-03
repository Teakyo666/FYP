// src/config/menu.js
// Menu configuration file

// Define menu items for each role
export const menuConfig = {
  // Common menus visible to all users
  common: [
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: 'dashboard/Home',
      icon: 'House',
      title: 'Home',
      roles: ['admin', 'volunteer', 'recycler', 'customer'],
      order: 0
    },
    {
      path: '/dashboard/garbage',
      name: 'GarbageQuery',
      component: 'dashboard/Garbage/query',
      icon: 'Search',
      title: 'Garbage Classification',
      roles: ['admin', 'volunteer', 'recycler', 'customer'],
      order: 0
    },
    {
      path: '/dashboard/garbage/placecheck',
      name: 'GarbagePlaceCheck',
      component: 'dashboard/Garbage/placecheck',
      icon: 'Location',
      title: 'Recycle Station Finder',
      roles: ['admin', 'volunteer', 'recycler', 'customer'],
      order: 0
    },
  ],
  
  // Administrator exclusive menus
  admin: [
    {
      path: '/dashboard/admin/garbage',
      name: 'AdminGarbage',
      component: 'dashboard/admin/garbage',
      icon: 'Delete',
      title: 'Garbage Management',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/user',
      name: 'AdminUser',
      component: 'dashboard/admin/user',
      icon: 'User',
      title: 'User Management',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/AI',
      name: 'AdminAI',
      component: 'dashboard/admin/AI',
      icon: 'HelpFilled',
      title: 'AI Generation Management',
      roles: ['admin'],
      order: 2
    },
    {
      path: '/dashboard/admin/apply',
      name: 'AdminApply',
      component: 'dashboard/admin/apply',
      icon: 'Check',
      title: 'Apply Role Management',
      roles: ['admin'],
      order: 2
    },
    
  ],
  
  // Volunteer exclusive menus
  volunteer: [
  ],
  
  // Recycler exclusive menus
  recycler: [
  ],
  
  // Customer exclusive menus
  customer: [
    
  ],
  
  // General function menus
  general: [
    {
      path: '/dashboard/garbage/sentence',
      name: 'GarbageSentence',
      component: 'dashboard/Garbage/sentence',
      icon: 'Edit',
      title: 'Sentence Management',
      roles: ['admin', 'volunteer'],
      order: 1
    },
    {
      path: '/dashboard/garbage/place',
      name: 'GarbagePlace',
      component: 'dashboard/Garbage/place',
      icon: 'Van',
      title: 'Recycle Station Management',
      roles: ['admin', 'recycler'],
      order: 1
    },
  ]
}

// Get menus accessible to the current user role
export const getUserMenus = (role) => {
  let menus = [].concat(menuConfig.common)
  
  switch (role) {
    case 'admin':
      menus = menus.concat(menuConfig.admin)
      break
    case 'volunteer':
      menus = menus.concat(menuConfig.volunteer)
      break
    case 'recycler':
      menus = menus.concat(menuConfig.recycler)
      break
    case 'customer':
      menus = menus.concat(menuConfig.customer)
      break
    default:
      break
  }
  
  // Add general function menus
  menus = menus.concat(menuConfig.general)
  
  // Sort by order property
  menus.sort((a, b) => (a.order || 0) - (b.order || 0))
  
  return menus
}